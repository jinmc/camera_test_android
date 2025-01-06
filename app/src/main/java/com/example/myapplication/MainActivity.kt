package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_permissions()
    }

    fun get_permissions() {
        // Get permissions
        var permissionsList = mutableListOf<String>()
        permissionsList.add(android.Manifest.permission.CAMERA)
        permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        // Request permissions
        requestPermissions(permissionsList.toTypedArray(), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}