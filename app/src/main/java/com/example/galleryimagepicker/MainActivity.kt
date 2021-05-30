package com.example.galleryimagepicker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    loadImage()
                } else {
                    //When user deny
                    Toast.makeText(this, "we need to load image", Toast.LENGTH_SHORT).show()
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
    }

    private fun requestPermission() {
        when  {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                loadImage()
            }
            shouldShowRequestPermissionRationale("Please allow so you can select image") -> {
                Toast.makeText(this, "thx", Toast.LENGTH_SHORT).show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun loadImage() {
        val utils = ImageQueryUtils(this)
        val albumList = utils.loadFolderNameList()

        Log.d("DEBUG_TEST", albumList.toString())
        albumList.forEach {
            Log.i("DEBUG_TEST", utils.loadAlbumImages(it).toString())
        }
    }
}