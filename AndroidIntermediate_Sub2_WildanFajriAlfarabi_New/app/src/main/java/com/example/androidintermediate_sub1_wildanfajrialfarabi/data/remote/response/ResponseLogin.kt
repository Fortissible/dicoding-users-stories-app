package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

    @field:SerializedName("loginResult")
	val loginResult: LoginResult,

    @field:SerializedName("error")
	val error: Boolean,

    @field:SerializedName("message")
	val message: String
)

data class LoginResult(

	@field:SerializedName("name")
	val name: String ?= null,

	@field:SerializedName("userId")
	val userId: String?= null,

	@field:SerializedName("token")
	val token: String?= null
)
