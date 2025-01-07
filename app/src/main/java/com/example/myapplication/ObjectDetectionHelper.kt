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
                .setScoreThreshold(0.3f)
                .setMaxResults(5)
        val options = optionsBuilder.build()

        objectDetector = ObjectDetector.createFromFileAndOptions(
            context,
            "efficientdet-lite2.tflite",
            options
        )
    }

    fun detect(bitmap: Bitmap): List<Detection> {
        val tensorImage = TensorImage.fromBitmap(bitmap)
        return objectDetector.detect(tensorImage)
    }
}