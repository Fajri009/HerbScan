package com.example.herbscan.tflite

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
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

class TFLiteHelper(private val context: Context) {
    private lateinit var interpreter: Interpreter

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

    fun softmax(logits: FloatArray): FloatArray {
        val expValues = logits.map { exp(it) }
        val sumExp = expValues.sum()
        return expValues.map { it / sumExp }.toFloatArray()
    }

    fun classifyImage(bitmap: Bitmap): Triple<String, String, String> {
        val startTime = System.currentTimeMillis() // Catat waktu mulai

        val imageTensorIndex = 0
        val imageShape = interpreter.getInputTensor(imageTensorIndex).shape()
        val imageDataType: DataType = interpreter.getInputTensor(imageTensorIndex).dataType()

        val inputImageBuffer = TensorImage(imageDataType)
            .loadImage(imageShape, bitmap)
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