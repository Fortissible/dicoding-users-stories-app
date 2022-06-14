package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.LoginSessionPreferences
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.Result
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.LoginResult
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseLogin
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ResponseRegister
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.RetrofitApiService
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.wrapEspressoIdlingResource
import retrofit2.Response

class UserRepository (
    private val context: Context,
    private val apiService: RetrofitApiService
){
    val loginResult = MutableLiveData<Response<ResponseLogin>>()
    val responseRegister = MutableLiveData<Response<ResponseRegister>>()

    fun getUserSessionToken(): String {
        val prefs = LoginSessionPreferences(context)
        val token = prefs.getLoginSession().token
        return context.getString(R.string.token_bearer, token)
    }

    fun clearLoginPreferences() : Result<Boolean>{
        val prefs = LoginSessionPreferences(context)
        prefs.clearLoginSession()
        return Result.Success(true)
    }

    fun setLoginPreferences(response : LoginResult){
        val prefs = LoginSessionPreferences(context)
        prefs.setLoginSession(response)
    }

    suspend fun loginUser(email : String, pass : String) {
        wrapEspressoIdlingResource {
            val prefs = LoginSessionPreferences(context)
            val responseLogin = apiService.loginUser(email,pass)
            if (responseLogin.isSuccessful && responseLogin.body()?.loginResult != null){
                prefs.setLoginSession(responseLogin.body()?.loginResult!!)
                loginResult.value = responseLogin
            }
            else {
                Log.e("Login Gagal", "onFailure: ${responseLogin.message()}")
                loginResult.value = responseLogin
            }
        }
    }

    suspend fun registerUser(email:String,pass:String,name:String){
        val responseLogin = apiService.registerUser(name,email,pass)
        if (responseLogin.isSuccessful && !responseLogin.body()!!.error){
            responseRegister.value = responseLogin
        }
        else {
            Log.e("Login Gagal", "onFailure: ${responseLogin.message()}")
            responseRegister.value = responseLogin
        }
    }
}