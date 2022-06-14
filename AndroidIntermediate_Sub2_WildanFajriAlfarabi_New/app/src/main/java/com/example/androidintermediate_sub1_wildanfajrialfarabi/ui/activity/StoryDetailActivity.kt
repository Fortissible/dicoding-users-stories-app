package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityStoryDetailBinding
import com.google.android.libraries.places.api.Places
import java.io.IOException
import java.util.*

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var storyDetailBinding: ActivityStoryDetailBinding
    private lateinit var detailName : TextView
    private lateinit var detailDesc : TextView
    private lateinit var detailLoc : TextView
    private lateinit var detailImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storyDetailBinding = ActivityStoryDetailBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(storyDetailBinding.root)
        viewBinding()

        val appInfo: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = appInfo.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val name = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA)
        setDetailStory(name)
    }

    private fun setDetailStory(data : ListStoryItem?){
        detailName.text = getString(R.string.upload_by_text,data?.name)
        detailDesc.text = data?.description
        Glide.with(this@StoryDetailActivity)
            .load(data?.photoUrl)
            .override(800, 800)
            .fitCenter()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.ic_baseline_error_outline_24_red)
            .into(detailImage)
        val stringLocation = reverseGeocoder(data?.lat,data?.lon)
        detailLoc.text = stringLocation
    }

    private fun reverseGeocoder(lat : Double?, lon : Double?) : String {
        val revGeocoder = Geocoder(applicationContext, Locale.getDefault())
        if (lat != null && lon != null) {
            try {
                val addressGeo: List<Address> = revGeocoder.getFromLocation(lat, lon, 1)
                val addressDecoded = buildString {
                    if (addressGeo.isNotEmpty()) {
                        append(getString(R.string.uploaded_from)).append("\n")
                        append(addressGeo[0].subAdminArea).append("\n")
                        append(addressGeo[0].locality).append(", ")
                        append(addressGeo[0].adminArea).append(", ")
                        append(addressGeo[0].countryName).append(", ")
                        append(addressGeo[0].postalCode)
                    }
                    else
                        append(failToDecodeCoordinates(lat,lon))
                }
                return addressDecoded
            } catch (e: IOException) {
                Toast.makeText(applicationContext,"Unable connect to Geocoder",Toast.LENGTH_SHORT).show()
                return failToDecodeCoordinates(lat,lon)
            }
        }
        else return failToDecodeCoordinates(lat,lon)
    }

    private fun failToDecodeCoordinates(lat: Double?, lon: Double?):String{
        return buildString {
            append(getString(R.string.uploaded_from)).append(" ")
            append(lat.toString()).append(" | ")
            append(lon.toString())
        }
    }

    private fun viewBinding(){
        detailName = storyDetailBinding.detailName
        detailDesc = storyDetailBinding.detailDesc
        detailImage = storyDetailBinding.detailImage
        detailLoc = storyDetailBinding.detailLocation
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}