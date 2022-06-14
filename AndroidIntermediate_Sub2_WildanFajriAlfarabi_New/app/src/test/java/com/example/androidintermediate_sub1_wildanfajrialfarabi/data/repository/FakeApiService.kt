package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository

import com.example.androidintermediate_sub1_wildanfajrialfarabi.DataDummy
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseAddStory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseGetStory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseLogin
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseRegister
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.RetrofitApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class FakeApiService: RetrofitApiService {

    private var dataDummy = DataDummy.generateDummyStories()
    private var responseSuccessLogin = DataDummy.generateSuccessLogin()
    private var responseFailedLogin = DataDummy.generateFailedLogin()
    private var responseSuccessRegister = DataDummy.generateSuccessRegister()
    private var responseFailedRegister = DataDummy.generateFailedRegister()

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Response<ResponseRegister> {
        return if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            responseFailedRegister
        } else {
            responseSuccessRegister
        }
    }

    override suspend fun loginUser(name: String, email: String): Response<ResponseLogin> {
        return if (name.isEmpty() || email.isEmpty()) {
            responseFailedLogin
        } else {
            responseSuccessLogin
        }
    }

    override fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: Float?,
        longitude: Float?
    ): Call<ResponseAddStory> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoryWithLoc(
        token: String,
        loc: Int?,
        page: Int?,
        size: Int?
    ): ResponseGetStory {
        when {
            token.isEmpty() -> {
                return ResponseGetStory(
                    listStory = listOf(),
                    error = true,
                    message = "Error token is empty"
                )
            }
            token == "not valid token" -> {
                return ResponseGetStory(
                    listStory = listOf(),
                    error = true,
                    message = "User not authenticated"
                )
            }
            else -> {
                return ResponseGetStory(
                    listStory = dataDummy,
                    error = false,
                    message = "Retrive Stories Success"
                )
            }
        }
    }
}