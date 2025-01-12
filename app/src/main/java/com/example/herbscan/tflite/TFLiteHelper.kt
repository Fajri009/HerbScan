package com.example.herbscan.tflite

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Collections

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

    fun classifyImage(bitmap: Bitmap): Pair<String, String> {
        val imageTensorIndex = 0
        val imageShape = interpreter.getInputTensor(imageTensorIndex).shape()
        val imageDataType: DataType = interpreter.getInputTensor(imageTensorIndex).dataType()

        val probabilityTensorIndex = 0
        val probabilityShape = interpreter.getOutputTensor(probabilityTensorIndex).shape()
        val probabilityDataType: DataType = interpreter.getOutputTensor(probabilityTensorIndex).dataType()

        val inputImageBuffer = TensorImage(imageDataType)
            .loadImage(imageShape, bitmap)
        val outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
        val probabilityProcessor = TensorProcessor.Builder().build()
        interpreter.run(inputImageBuffer!!.buffer, outputProbabilityBuffer.buffer.rewind())

        val labels = try {
            FileUtil.loadLabels(context, LABEL_PATH)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair("", "0f")
        }

        val labelProbability = labels.let {
            TensorLabel(it, probabilityProcessor!!.process(outputProbabilityBuffer))
                .mapWithFloatValue
        }

        val maxValueInMap: Float = Collections.max(labelProbability.values)

        val found = labelProbability.entries.find {
            it.value == maxValueInMap
        }
        Log.i(TAG, "classifyImage: ${found?.key.orEmpty()}")

        val probabilityPercentage = (maxValueInMap * 10).toInt()
        return found?.key.orEmpty() to "$probabilityPercentage%"
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