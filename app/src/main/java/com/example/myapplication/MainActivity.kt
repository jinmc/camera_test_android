package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import com.example.myapplication.ObjectDetectionHelper

class MainActivity : AppCompatActivity() {

    lateinit var capReq: CaptureRequest.Builder
    lateinit var handler: Handler
    lateinit var handlerThread: HandlerThread
    lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var cameraCaptureSession: CameraCaptureSession
    private var cameraDevice: CameraDevice? = null
    lateinit var captureRequest: CaptureRequest
    lateinit var imageReader: ImageReader
    private lateinit var objectDetector: ObjectDetectionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_permissions()

        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        objectDetector = ObjectDetectionHelper(this) // ObjectDetector 초기화

        imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener{
            override fun onImageAvailable(reader: ImageReader) {
                var image = reader.acquireLatestImage()
                image?.let {
                    var buffer = image.planes[0].buffer
                    var bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)


                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                    val rotatedBitmap = rotateBitmap(bitmap, 90f)
                    val rotatedBitmap = bitmap

                    // Measure start time
                    val startTime = System.currentTimeMillis()
//                    val startTime = System.nanoTime()

                    // Object Detection execution
                    val detectionResults = objectDetector.detect(rotatedBitmap)

                    val endTime = System.currentTimeMillis()
//                    val endTime = System.nanoTime()

                    // Calculate and log the duration
                    val duration = endTime - startTime

                    println("ObjectDetection : Object detection took $duration ms")

                    detectionResults.forEach { detection ->
                        val boundingBox = detection.boundingBox
                        val category = detection.categories.firstOrNull()

                        if (category != null) {
                            val label = category.label
                            val confidence = category.score
                            println("Detected $label with confidence $confidence at $boundingBox")
                        }
                    }

//                var file = File(Environment.getExternalStorageDirectory().toString() + "/thisimage.jpg")
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val file = File(picturesDir, "thisimage.jpg")
                    val opStream = FileOutputStream(file)
//                val file = File(getExternalFilesDir(null), "thisimage.jpg")
//                opStream.write(bytes)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, opStream)

                    opStream.close()
                    image.close()
                    Toast.makeText(this@MainActivity, "Image captured", Toast.LENGTH_SHORT).show()

                    it.close()
                }
            }
        }, handler)

        textureView = findViewById(R.id.textureView)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        findViewById<Button>(R.id.capture).apply {
            setOnClickListener {
                cameraDevice?.let {
                    capReq = it.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                    capReq.addTarget(imageReader.surface)
                    cameraCaptureSession.capture(capReq.build(), null, handler)
                } ?: run {
                    Toast.makeText(this@MainActivity, "Camera not ready", Toast.LENGTH_SHORT).show()
                }

            }
        }

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                open_camera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                // Handle size change if needed
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                // Handle texture update if needed
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice?.close()
        handlerThread.quitSafely()
    }

    @SuppressLint("MissingPermission")
    fun open_camera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraIdList = cameraManager.cameraIdList
            if (cameraIdList.isNotEmpty()) {
                cameraManager.openCamera(cameraIdList[0], object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        capReq = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                        val surface = Surface(textureView.surfaceTexture)
                        capReq.addTarget(surface)

                        camera.createCaptureSession(listOf(surface, imageReader.surface), object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                cameraCaptureSession = session
                                captureRequest = capReq.build()
                                cameraCaptureSession.setRepeatingRequest(captureRequest, null, handler)
                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {
                                // Handle configuration failure
                            }
                        }, handler)
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        cameraDevice?.close()
                        cameraDevice = null
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        cameraDevice?.close()
                        cameraDevice = null
                    }
                }, handler)
            } else {
                Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
        }
    }

    fun get_permissions() {
        val permissionsList = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsList.toTypedArray(), 101)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            open_camera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}