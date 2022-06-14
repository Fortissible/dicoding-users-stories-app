package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.createFile
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityCameraXactivityBinding
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXActivity : AppCompatActivity() {
    private lateinit var cameraXactivityBinding: ActivityCameraXactivityBinding
    private lateinit var takePhoto : ImageView
    private lateinit var switchCamera : ImageView
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraXactivityBinding = ActivityCameraXactivityBinding.inflate(layoutInflater)
        setContentView(cameraXactivityBinding.root)
        supportActionBar?.hide()

        takePhoto = cameraXactivityBinding.takePhoto
        switchCamera = cameraXactivityBinding.switchCamera
        cameraExecutor = Executors.newSingleThreadExecutor()

        switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            openCamera()
        }
        takePhoto.setOnClickListener {
            takePhoto()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        openCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun openCamera(){
        val camProviderFuture = ProcessCameraProvider.getInstance(this@CameraXActivity)
        camProviderFuture.addListener({
            val camProvider : ProcessCameraProvider = camProviderFuture.get()
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(cameraXactivityBinding.cameraView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            try{
                camProvider.unbindAll()
                camProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exception: Exception){
                Toast.makeText(this,"Gagal memanggil camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        val imageCaptured = imageCapture?:return
        val photoFile= createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCaptured.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object:ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("pic", photoFile)
                    intent.putExtra("backCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    setResult(AddStoriesActivity.CAMERA_X_RESULT,intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraXActivity, "Gagal Ambil Gambar", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}