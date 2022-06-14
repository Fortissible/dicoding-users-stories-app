package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.*
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.APIConfig
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository.StoriesRepository
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository.UserRepository
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.ErrorException
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.reduceFileImage
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel(
    private val storiesRepository: StoriesRepository,
    private val userRepository: UserRepository) :ViewModel()
{
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    private val _previewImage = MutableLiveData<Bitmap>()
    private val _uriImage = MutableLiveData<Uri>()
    private val _responseAddStory = MutableLiveData<ResponseAddStory>()
    private val _imageFile = MutableLiveData<File>()
    private val _errorMessage = MutableLiveData<String>()

    val listUser : LiveData<List<ListStoryItem>> = _listStory
    val previewImage : LiveData<Bitmap> = _previewImage
    val uriImage : LiveData<Uri> = _uriImage
    val responseAddStory : LiveData<ResponseAddStory> = _responseAddStory
    val imageFile : LiveData<File> = _imageFile
    val errorMessage : LiveData<String> = _errorMessage
    val responseRegister : LiveData<Response<ResponseRegister>> = userRepository.responseRegister
    val responseLogin : LiveData<Response<ResponseLogin>> = userRepository.loginResult

    private val token = userRepository.getUserSessionToken()

    val stories: LiveData<PagingData<ListStoryItem>> =
        storiesRepository.getStories(token).cachedIn(viewModelScope)

    fun getPreviewImage(bitmap : Bitmap){
        try {
            _previewImage.value = bitmap
        } catch (e: ErrorException) {
            _errorMessage.value = e.message.toString()
        }
    }

    fun getPreviewImageByURI(uri : Uri){
        _uriImage.value = uri
    }

    fun uploadMultipart(imgFile:File?, desc:String, latLng: List<Float>) {
        if (imgFile != null) {
            val token = userRepository.getUserSessionToken()
            val fileComp = reduceFileImage(imgFile)
            val description = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = fileComp.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", fileComp.name, requestImageFile
                )
            val service = if (latLng.isEmpty()){
                APIConfig.getApiService().addStory(
                    token, imageMultipart, description)
            } else {
                APIConfig.getApiService().addStory(
                    token, imageMultipart, description,
                    latLng[0], latLng[1])
            }
            service.enqueue(object : Callback<ResponseAddStory> {
                override fun onResponse(
                    call: Call<ResponseAddStory>,
                    response: Response<ResponseAddStory>
                ) {
                    if (response.isSuccessful) {
                        _responseAddStory.value = response.body()
                    } else {
                        _responseAddStory.value = response.body()
                    }
                }
                override fun onFailure(call: Call<ResponseAddStory>, t: Throwable) {
                    _errorMessage.value = t.message.toString()
                }
            })
        } else {
            _errorMessage.value = "Insert or Take image first!"
        }
    }

    fun loginUser(email: String, pass: String) {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                try {
                    userRepository.loginUser(email,pass)
                } catch (e : ErrorException) {
                    _errorMessage.value = e.message.toString()
                }
            }
        }
    }

    fun registerUser(email: String, pass: String, name: String){
        viewModelScope.launch {
            try {
                userRepository.registerUser(email,pass,name)
            } catch (e : ErrorException) {
                _errorMessage.value = e.message.toString()
            }
        }
    }

    fun clearLoginPreferences() = userRepository.clearLoginPreferences()

    suspend fun getStoriesFromDb(){
        _listStory.value = storiesRepository.getStoriesFromDb()
    }
}