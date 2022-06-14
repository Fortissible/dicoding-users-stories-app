package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local

import android.content.Context
import androidx.core.content.edit
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.LoginResult

internal class LoginSessionPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLoginSession(response : LoginResult){
        preferences.edit {
            putString(NAME,response.name)
            putString(USERID,response.userId)
            putString(TOKEN,response.token)
        }
    }

    fun getLoginSession(): LoginResult{
        return LoginResult(
            name = preferences.getString(NAME, EMPTY),
            userId = preferences.getString(USERID, EMPTY),
            token = preferences.getString(TOKEN, EMPTY)
        )
    }

    fun clearLoginSession(){
        val edt = preferences.edit()
        edt.clear()
        edt.apply()
    }

    fun checkLoginSession(): Boolean{
        val name = preferences.getString(NAME,"")
        val userid = preferences.getString(USERID,"")
        val token = preferences.getString(TOKEN,"")
        return (name.isNullOrEmpty() &&
                userid.isNullOrEmpty()  &&
                token.isNullOrEmpty())
    }

    companion object {
        private const val PREFS_NAME = "login_prefs"
        private const val NAME = "name"
        private const val USERID = "userid"
        private const val TOKEN = "token"
        private const val EMPTY = ""
    }
}