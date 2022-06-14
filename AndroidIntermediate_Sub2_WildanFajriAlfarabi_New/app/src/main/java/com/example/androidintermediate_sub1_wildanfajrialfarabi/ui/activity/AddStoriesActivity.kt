package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.LoginSessionPreferences
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ImageFile
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityAddStoriesBinding
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.MainViewModel
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.ViewModelFactory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.bitmapToFile
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.rotateBitmap
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.uriToFile
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class AddStoriesActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var addStoriesBinding: ActivityAddStoriesBinding
    private lateinit var cameraXButton : Button
    private lateinit var intentGalleryButton : Button
    private lateinit var intentCameraButton : Button
    private lateinit var uploadButton : FloatingActionButton
    private lateinit var descEditText : EditText
    private lateinit var currentPhotoDir: String
    private lateinit var mapsButton : FloatingActionButton
    private lateinit var locationText : TextView
    private lateinit var prefs : LoginSessionPreferences

    private var location = mutableListOf<Float>()
    private var isBackCamera = true
    private var fileTemp: File? = null
    private val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoriesBinding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(addStoriesBinding.root)
        supportActionBar?.hide()
        prefs = LoginSessionPreferences(this)
        viewBinding()

        if (intent.getBooleanExtra(MapsActivity.EXTRA_LOC,false)){
            val imageFile : ImageFile = (intent.getParcelableExtra(MapsActivity.EXTRA_IMAGE_FILE))!!
            fileTemp = imageFile.imageFile
            if (!fileTemp?.path.isNullOrEmpty())
                mainViewModel.getPreviewImage(BitmapFactory.decodeFile(fileTemp!!.path))

            location.apply {
                clear()
                add(intent.getDoubleExtra(MapsActivity.EXTRA_LOC_LAT,0.0).toFloat())
                add(intent.getDoubleExtra(MapsActivity.EXTRA_LOC_LON,0.0).toFloat())
            }
            setLocationText()
        }

        mainViewModel.previewImage.observe(this){ bitmap ->
            addStoriesBinding.photoResult.setImageBitmap(bitmap)
        }
        mainViewModel.uriImage.observe(this){ uri ->
            addStoriesBinding.photoResult.setImageURI(uri)
        }
        mainViewModel.responseAddStory.observe(this){
            if (!it.error) {
                Toast.makeText(this, "Story uploaded!", Toast.LENGTH_SHORT).show()
                val intentRegisterActivity = Intent(this@AddStoriesActivity, StoriesActivity::class.java)
                startActivity(intentRegisterActivity)
            } else {
                Toast.makeText(this, "Fail to upload Story!", Toast.LENGTH_SHORT).show()
            }
        }
        mainViewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        mainViewModel.imageFile.observe(this){
            fileTemp = it
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraXButton.setOnClickListener(this)
        intentCameraButton.setOnClickListener(this)
        intentGalleryButton.setOnClickListener(this)
        uploadButton.setOnClickListener(this)
        mapsButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            cameraXButton.id -> {
                val intentToCameraXActivity = Intent(this, CameraXActivity::class.java)
                launcherIntentCameraX.launch(intentToCameraXActivity)
            }
            intentCameraButton.id -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.resolveActivity(packageManager)
                com.example.androidintermediate_sub1_wildanfajrialfarabi.util.createTempFile(application)
                    .also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this@AddStoriesActivity,
                            "com.example.androidintermediate_sub1_wildanfajrialfarabi",
                            it
                        )
                        currentPhotoDir = it.absolutePath
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        launcherIntentCamera.launch(intent)
                    }
            }
            intentGalleryButton.id -> {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                launcherIntentGallery.launch(chooser)
            }
            uploadButton.id -> {
                mainViewModel.uploadMultipart(fileTemp,descEditText.text.toString(),location)
            }
            mapsButton.id -> {
                val intentToMapsActivity = Intent(this,MapsActivity::class.java)
                val imageFile = ImageFile(fileTemp)
                intentToMapsActivity.putExtra(MapsActivity.EXTRA_IMAGE_FILE,imageFile)
                startActivity(intentToMapsActivity)
            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("pic") as File
            isBackCamera = it.data?.getBooleanExtra("backCamera", true) as Boolean
            fileTemp = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(fileTemp?.path),
                isBackCamera
            )
            fileTemp = bitmapToFile(fileTemp!!,result)
            mainViewModel.getPreviewImage(result)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoDir)
            fileTemp = myFile
            val result = BitmapFactory.decodeFile(fileTemp?.path)
            mainViewModel.getPreviewImage(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoriesActivity)
            fileTemp = myFile
            mainViewModel.getPreviewImageByURI(selectedImg)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun viewBinding(){
        intentGalleryButton = addStoriesBinding.intentGallery
        intentCameraButton = addStoriesBinding.intentCamera
        cameraXButton = addStoriesBinding.cameraX
        uploadButton = addStoriesBinding.uploadButton
        descEditText = addStoriesBinding.postDesc
        mapsButton = addStoriesBinding.mapsButton
        locationText = addStoriesBinding.locationText
    }

    private fun setLocationText(){
        locationText.visibility = View.VISIBLE
        locationText.text = buildString {
            append("Upload with location:\n")
            append(location[0].toString())
            append(" | ")
            append(location[1].toString())
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}