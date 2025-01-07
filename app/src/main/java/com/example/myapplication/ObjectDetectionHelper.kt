package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectDetectionHelper(context: Context) {

    private val objectDetector: ObjectDetector

    init {
        val optionsBuilder =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(0.5f)
                .setMaxResults(3)
        val options = optionsBuilder.build()

        objectDetector = ObjectDetector.createFromFileAndOptions(
            context,
            "model.tflite",
            options
        )
    }

    fun detect(bitmap: Bitmap): List<Detection> {
        val tensorImage = TensorImage.fromBitmap(bitmap)
        return objectDetector.detect(tensorImage)
    }
}