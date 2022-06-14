package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ImageFile(
    var imageFile: File? = null
): Parcelable
