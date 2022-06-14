package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit

import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseAddStory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseGetStory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseLogin
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") name: String,
        @Field("password") email: String
    ): Response<ResponseLogin>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token : String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Float? = null,
        @Part("lon") longitude: Float? = null
    ): Call<ResponseAddStory>

    @GET("stories")
    suspend fun getStoryWithLoc(
        @Header("Authorization") token : String,
        @Query("location") loc: Int ?= 0,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): ResponseGetStory
}