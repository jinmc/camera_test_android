package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DisplayImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val imageView: ImageView = findViewById(R.id.imageView)
        val backButton: Button = findViewById(R.id.backButton)

        val byteArray = intent.getByteArrayExtra("image")
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        val detections = intent.getParcelableArrayListExtra<Detection>("detections")

        bitmap?.let {
            val bitmapWithBoundingBoxes = drawBoundingBoxes(it, detections)
            imageView.setImageBitmap(bitmapWithBoundingBoxes)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun drawBoundingBoxes(bitmap: Bitmap, detections: List<Detection>?): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        detections?.forEach { detection ->
            canvas.drawRect(detection.boundingBox, paint)
        }

        return mutableBitmap
    }
}