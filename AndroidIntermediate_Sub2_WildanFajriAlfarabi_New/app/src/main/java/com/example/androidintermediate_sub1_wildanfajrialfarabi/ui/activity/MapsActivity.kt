package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ImageFile
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityMapsBinding
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.MainViewModel
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var useLocButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imageFile : ImageFile

    private var title = "I'm Here"
    private var snippet = "Hello World!"
    private var latLng = LatLng(0.0,0.0)
    private val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        useLocButton = binding.useButton
        useLocButton.visibility = View.GONE
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!intent.getBooleanExtra(EXTRA_FROM_STORIES,false)){
            imageFile = (intent.getParcelableExtra(EXTRA_IMAGE_FILE))!!
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
        mMap.uiSettings.apply {
            isCompassEnabled = true
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isMapToolbarEnabled = true
        }

        if (!intent.getBooleanExtra(EXTRA_FROM_STORIES,false)){
            useLocButton.visibility = View.VISIBLE
            getMyLoc()
            mMap.setOnMapLongClickListener {
                latLng = LatLng(it.latitude,it.longitude)
                addMarker()
            }
            useLocButton.setOnClickListener {
                val intentToAddStories = Intent(this,AddStoriesActivity::class.java)
                intentToAddStories.apply {
                    putExtra(EXTRA_IMAGE_FILE,imageFile)
                    putExtra(EXTRA_LOC,true)
                    putExtra(EXTRA_LOC_LAT,latLng.latitude)
                    putExtra(EXTRA_LOC_LON,latLng.longitude)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intentToAddStories)
            }
        } else {
            getMyLoc()
            lifecycleScope.launch {
                mainViewModel.getStoriesFromDb()
            }
            mainViewModel.listUser.observe(this){
                setAllStoriesMarker(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_map_type,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_theme -> {
                return true
            }
            R.id.day_mode -> {
                try {
                    val result = mMap.setMapStyle(MapStyleOptions("[]"))
                    if (!result) Toast.makeText(this, "Failed to change the theme", Toast.LENGTH_SHORT).show()
                } catch (exception : Exception){
                    Log.e(TAG, "Theme not found", exception)
                }
                return true
            }
            R.id.dark_mode -> {
                try {
                    val result = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style_dark))
                    if (!result) Toast.makeText(this, "Failed to change the theme", Toast.LENGTH_SHORT).show()
                } catch (exception : Exception){
                    Log.e(TAG, "Theme not found", exception)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){ permission ->
        when {
            permission[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false ->
                getMyLoc()
            permission[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false ->
                getMyLoc()
            else -> {}
        }
    }

    private fun checkPermission(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(this@MapsActivity,
            permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLoc(){
        if (checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                mMap.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        latLng = LatLng(location.latitude,location.longitude)
                        addMarker()
                    } else {
                        Toast.makeText(
                            this@MapsActivity,
                            "Location not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 0f))
                }
        }
        else{
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun addMarker() {
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
        )
    }

    private fun setAllStoriesMarker(listStories : List<ListStoryItem>){
        for (list in listStories){
            val lat = list.lat
            val lon = list.lon
            latLng = LatLng(lat,lon)
            title = list.name
            snippet = list.description
            addMarker()
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 0f))
    }

    companion object {
        const val EXTRA_LOC_LAT = "loc_lat"
        const val EXTRA_LOC_LON = "loc_lon"
        const val EXTRA_LOC = "loc_provided"
        const val EXTRA_FROM_STORIES = "from_stories"
        const val EXTRA_IMAGE_FILE = "image_file"
    }
}