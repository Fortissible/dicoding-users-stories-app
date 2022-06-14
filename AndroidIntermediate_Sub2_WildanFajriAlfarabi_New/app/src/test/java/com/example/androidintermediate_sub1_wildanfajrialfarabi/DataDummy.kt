package com.example.androidintermediate_sub1_wildanfajrialfarabi

import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.LoginResult
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseLogin
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseRegister
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object DataDummy {
    fun generateDummyStories(): List<ListStoryItem> {
        val newsList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val news = ListStoryItem(
                "id-$i",
                "https://dummy.com/image.jpg",
                "2022-04-22T13:56:32.423Z",
                "tester1",
                "TestingUnitViewModel",
                1.0+i,
                1.0+i
            )
            newsList.add(news)
        }
        return newsList
    }

    fun generateSuccessLogin(): Response<ResponseLogin> {
        return Response.success(
            ResponseLogin(
                loginResult = LoginResult("SuccessName", "SuccessID", "SuccessToken"),
                error = false,
                message = "Login Success"
            )
        )
    }

    fun generateFailedLogin(): Response<ResponseLogin> {
        return Response.error(401,"".toResponseBody("text/plain".toMediaType()))
    }

    fun generateSuccessRegister(): Response<ResponseRegister> {
        return Response.success(
            ResponseRegister(
                error = false,
                message = "Register Success"
            )
        )
    }

    fun generateFailedRegister(): Response<ResponseRegister> {
        return Response.error(401,"".toResponseBody("text/plain".toMediaType()))
    }
}