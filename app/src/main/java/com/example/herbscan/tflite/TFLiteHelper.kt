package com.example.herbscan.tflite

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.exp
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.min

class TFLiteHelper(private val context: Context) {
    private lateinit var interpreter: Interpreter
    private var applyCLAHE = false
    private var claheClipLimit = 4.0f
    private var claheTilesX = 8
    private var claheTilesY = 8
    private var applyHSVConversion = false

    // Data augmentation settings
    private var applyDataAugmentation = false
    private var rotationDegrees = 0f
    private var horizontalFlip = false
    private var verticalFlip = false
    private var brightnessAdjustment = 0f

    init {
        try {
            val options = Interpreter.Options()
            interpreter = Interpreter(loadModelFile(context as Activity), options)
            Log.i(TAG, "Interpreter successfully initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize interpreter: ${e.message}")
            e.printStackTrace()
        }
    }

    fun enableCLAHE(enable: Boolean, clipLimit: Float = 4.0f, tilesX: Int = 8, tilesY: Int = 8) {
        applyCLAHE = enable
        claheClipLimit = clipLimit
        claheTilesX = tilesX
        claheTilesY = tilesY
        Log.i(TAG, "CLAHE ${if (enable) "enabled" else "disabled"} with clipLimit=$clipLimit, tiles=${tilesX}x${tilesY}")
    }

    fun enableHSVConversion(enable: Boolean) {
        applyHSVConversion = enable
        Log.i(TAG, "HSV conversion ${if (enable) "enabled" else "disabled"}")
    }

    fun enableDataAugmentation(
        enable: Boolean,
        rotation: Float = 0f,
        hFlip: Boolean = false,
        vFlip: Boolean = false,
        brightness: Float = 0f
    ) {
        applyDataAugmentation = enable
        rotationDegrees = rotation % 360
        horizontalFlip = hFlip
        verticalFlip = vFlip
        brightnessAdjustment = brightness.coerceIn(-1.0f, 1.0f)

        Log.i(TAG, "Data augmentation ${if (enable) "enabled" else "disabled"} with:" +
                " rotation=$rotationDegrees°," +
                " horizontalFlip=$horizontalFlip," +
                " verticalFlip=$verticalFlip," +
                " brightness=$brightnessAdjustment")
    }

    // Convert RGB to HSV and back to RGB (useful for color space manipulation)
    private fun convertRGBtoHSVtoRGB(src: Bitmap): Bitmap {
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "[HSV] Starting RGB->HSV->RGB conversion...")

        val width = src.width
        val height = src.height
        val result = Bitmap.createBitmap(width, height, src.config ?: Bitmap.Config.ARGB_8888)

        var pixelCount = 0
        var avgHue = 0.0
        var avgSaturation = 0.0
        var avgValue = 0.0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = src.getPixel(x, y)
                val r = Color.red(pixel) / 255.0f
                val g = Color.green(pixel) / 255.0f
                val b = Color.blue(pixel) / 255.0f

                // Convert RGB to HSV
                val hsv = FloatArray(3)
                Color.RGBToHSV((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), hsv)

                // Accumulate for averaging
                avgHue += hsv[0]
                avgSaturation += hsv[1]
                avgValue += hsv[2]

                // Convert HSV back to RGB
                val rgb = Color.HSVToColor(hsv)
                result.setPixel(x, y, rgb)

                pixelCount++
            }
        }

        avgHue /= pixelCount
        avgSaturation /= pixelCount
        avgValue /= pixelCount

        val processingTime = System.currentTimeMillis() - startTime

        Log.d(TAG, "[HSV] Conversion complete: $processingTime ms")
        Log.d(TAG, "[HSV] Average HSV: (${avgHue.toInt()}°, ${(avgSaturation * 100).toInt()}%, ${(avgValue * 100).toInt()}%)")
        Log.d(TAG, "[HSV] Processed $pixelCount pixels")

        return result
    }

    // Untuk meningkatkan kualitas gambar dengan memperbaiki kontras lokal (area yang pencahayaannya kurang merata)
    private fun applyCLAHE(src: Bitmap): Bitmap? {
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "Starting CLAHE with clipLimit=$claheClipLimit, tiles=${claheTilesX}x${claheTilesY}")

        val width = src.width
        val height = src.height

        // Create a copy of the bitmap to modify
        val result = src.config?.let { Bitmap.createBitmap(width, height, it) }

        // Convert to grayscale and get histogram
        val grayPixels = IntArray(width * height)
        val histogramGray = IntArray(256)

        // Step 1: Convert to grayscale
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = src.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                // Luminance formula for grayscale
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt().coerceIn(0, 255)
                grayPixels[y * width + x] = gray
                histogramGray[gray]++
            }
        }

        // Step 2: Divide the image into tiles
        val tileWidth = width / claheTilesX
        val tileHeight = height / claheTilesY

        // Function to create a CLAHE mapping for a tile
        fun createCLAHEMap(startX: Int, startY: Int, tileW: Int, tileH: Int): IntArray {
            // Create local histogram
            val localHist = IntArray(256)
            val pixelCount = tileW * tileH

            // Count pixels in this tile
            for (y in startY until min(startY + tileH, height)) {
                for (x in startX until min(startX + tileW, width)) {
                    val gray = grayPixels[y * width + x]
                    localHist[gray]++
                }
            }

            // Apply clip limit
            val clipLimit = (claheClipLimit * pixelCount / 256).toInt()
            var clippedPixels = 0

            // Clip histogram and count clipped pixels
            for (i in 0..255) {
                if (localHist[i] > clipLimit) {
                    clippedPixels += localHist[i] - clipLimit
                    localHist[i] = clipLimit
                }
            }

            // Redistribute clipped pixels
            val redistributePerBin = clippedPixels / 256
            for (i in 0..255) {
                localHist[i] += redistributePerBin
            }

            // Create cumulative histogram as the mapping function
            val map = IntArray(256)
            var sum = 0
            for (i in 0..255) {
                sum += localHist[i]
                // Scale the CDF to [0, 255]
                map[i] = if (pixelCount > 0) (sum * 255.0 / pixelCount).toInt().coerceIn(0, 255) else i
            }

            return map
        }

        // Generate mapping for each tile
        val tileMaps = Array(claheTilesY) { y ->
            Array(claheTilesX) { x ->
                createCLAHEMap(
                    x * tileWidth,
                    y * tileHeight,
                    tileWidth,
                    tileHeight
                )
            }
        }

        // Step 3: Apply bilinear interpolation between tiles to avoid boundary artifacts
        for (y in 0 until height) {
            for (x in 0 until width) {
                val tileX = min(x / tileWidth, claheTilesX - 1)
                val tileY = min(y / tileHeight, claheTilesY - 1)

                // Calculate interpolation weights
                val tx = (x % tileWidth) / tileWidth.toFloat()
                val ty = (y % tileHeight) / tileHeight.toFloat()

                // Get neighboring tile indices (handle edge cases)
                val tileX2 = min(tileX + 1, claheTilesX - 1)
                val tileY2 = min(tileY + 1, claheTilesY - 1)

                val srcPixel = src.getPixel(x, y)
                val r = Color.red(srcPixel)
                val g = Color.green(srcPixel)
                val b = Color.blue(srcPixel)
                val gray = grayPixels[y * width + x]

                // Get mappings from the four surrounding tiles
                val map1 = tileMaps[tileY][tileX][gray]
                val map2 = tileMaps[tileY][tileX2][gray]
                val map3 = tileMaps[tileY2][tileX][gray]
                val map4 = tileMaps[tileY2][tileX2][gray]

                // Bilinear interpolation
                val newGray = (
                        map1 * (1 - tx) * (1 - ty) +
                                map2 * tx * (1 - ty) +
                                map3 * (1 - tx) * ty +
                                map4 * tx * ty
                        ).toInt().coerceIn(0, 255)

                // Preserve color ratio
                val scale = if (gray > 0) newGray.toFloat() / gray else 1.0f
                val newR = (r * scale).toInt().coerceIn(0, 255)
                val newG = (g * scale).toInt().coerceIn(0, 255)
                val newB = (b * scale).toInt().coerceIn(0, 255)

                result!!.setPixel(x, y, Color.rgb(newR, newG, newB))
            }
        }

        val processingTime = System.currentTimeMillis() - startTime
        Log.d(TAG, "CLAHE processing complete: $processingTime ms")
        Log.d(TAG, "CLAHE result: width=${result?.width}, height=${result?.height}")

        return result
    }

    private fun applyDataAugmentation(src: Bitmap): Bitmap {
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "[Augmentation] Starting image augmentation...")

        var result = src

        // Create transformation matrix
        val matrix = Matrix()

        // Apply rotation if needed
        if (rotationDegrees != 0f) {
            matrix.postRotate(rotationDegrees)
        }

        // Apply flips if needed
        val scaleX = if (horizontalFlip) -1f else 1f
        val scaleY = if (verticalFlip) -1f else 1f

        if (horizontalFlip || verticalFlip) {
            // When flipping, we need to adjust the pivot point
            matrix.postScale(scaleX, scaleY, src.width / 2f, src.height / 2f)
        }

        // Apply matrix transformation if any transform is needed
        if (rotationDegrees != 0f || horizontalFlip || verticalFlip) {
            result = Bitmap.createBitmap(
                src,
                0, 0,
                src.width, src.height,
                matrix,
                true
            )
        }

        // Apply brightness adjustment if needed
        if (brightnessAdjustment != 0f) {
            result = adjustBrightness(result, brightnessAdjustment)
        }

        val processingTime = System.currentTimeMillis() - startTime
        Log.d(TAG, "[Augmentation] Completed in $processingTime ms")

        return result
    }

    private fun adjustBrightness(src: Bitmap, value: Float): Bitmap {
        // Convert -1.0 to 1.0 scale to a 0-255 scale for the ColorMatrix
        val adjustedValue = value * 100f

        val cm = ColorMatrix()
        cm.set(
            floatArrayOf(
                1f, 0f, 0f, 0f, adjustedValue,
                0f, 1f, 0f, 0f, adjustedValue,
                0f, 0f, 1f, 0f, adjustedValue,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val result = Bitmap.createBitmap(src.width, src.height, src.config!!)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)

        return result
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val expValues = logits.map { exp(it) }
        val sumExp = expValues.sum()
        return expValues.map { it / sumExp }.toFloatArray()
    }

    fun classifyImage(bitmap: Bitmap): Triple<String, String, String> {
        val startTime = System.currentTimeMillis() // Catat waktu mulai

        Log.i(TAG, "applyCLAHE: $applyCLAHE")
        Log.i(TAG, "applyHSVConversion: $applyHSVConversion")
        Log.i(TAG, "applyDataAugmentation: $applyDataAugmentation")

        // Apply HSV conversion first if enabled
        var processedBitmap = if (applyHSVConversion) {
            Log.i(TAG, "Applying HSV color conversion")
            convertRGBtoHSVtoRGB(bitmap)
        } else {
            Log.i(TAG, "No HSV conversion applied")
            bitmap
        }

        // Then apply CLAHE if enabled
        processedBitmap = if (applyCLAHE) {
            Log.i(TAG, "Applying CLAHE preprocessing to image")
            applyCLAHE(processedBitmap) ?: processedBitmap
        } else {
            Log.i(TAG, "CLAHE preprocessing is disabled")
            processedBitmap
        }

        // Apply data augmentation if enabled - THIS IS THE ADDITION!
        processedBitmap = if (applyDataAugmentation) {
            Log.i(TAG, "Applying data augmentation to image")
            applyDataAugmentation(processedBitmap)
        } else {
            Log.i(TAG, "Data augmentation is disabled")
            processedBitmap
        }

        val imageTensorIndex = 0
        val imageShape = interpreter.getInputTensor(imageTensorIndex).shape()
        val imageDataType: DataType = interpreter.getInputTensor(imageTensorIndex).dataType()

        val inputImageBuffer = TensorImage(imageDataType)
            .loadImage(imageShape, processedBitmap)
        val outputBuffer = TensorBuffer.createFixedSize(interpreter.getOutputTensor(0).shape(), interpreter.getOutputTensor(0).dataType())
        interpreter.run(inputImageBuffer!!.buffer, outputBuffer.buffer.rewind())

        val labels = try {
            FileUtil.loadLabels(context, LABEL_PATH)
        } catch (e: Exception) {
            e.printStackTrace()
            return Triple("", "", "0f")
        }

        var outputArray = outputBuffer.floatArray

        outputArray = softmax(outputArray)

        for (i in outputArray.indices) {
            Log.d(TAG, "Label: ${labels[i]} - Probability: ${outputArray[i]}")
        }

        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
        val foundLabel = if (maxIndex >= 0) labels[maxIndex] else ""
        val confidence = if (maxIndex >= 0) outputArray[maxIndex] else 0.0f

        val inferenceTime = System.currentTimeMillis() - startTime
        val inferenceTimeSeconds = inferenceTime / 1000.0
        Log.d(TAG, "Inference time: $inferenceTime ms")

        Log.i(TAG, "Classified as: $foundLabel with probability: ${"%.4f".format(confidence)} and inference time: $inferenceTimeSeconds seconds")

        return Triple(foundLabel, "%.4f".format(confidence), "$inferenceTimeSeconds seconds")
    }

    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun TensorImage.loadImage(imageShape: IntArray, bitmap: Bitmap): TensorImage? {
        this.load(bitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(imageShape[1], imageShape[2], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(getPreprocessNormalizeOp())
            .build()
        return imageProcessor.process(this)
    }

    private fun getPreprocessNormalizeOp(): TensorOperator {
        return NormalizeOp(0.0f, 255.0f)
    }

    companion object {
        private const val TAG = "TFLiteHelper"
        private const val MODEL_PATH = "model_convnext.tflite"
        private const val LABEL_PATH = "herbscan_label.txt"
    }
}