package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.LoginSessionPreferences
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityMainBinding
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.MainViewModel
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.ViewModelFactory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview.InsertEmailText
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview.InsertPasswordText
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview.LoginButton
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.wrapEspressoIdlingResource

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var emailEditText: InsertEmailText
    private lateinit var passwordEditText: InsertPasswordText
    private lateinit var loginButton: LoginButton
    private lateinit var daftarStringButton : TextView
    private lateinit var loadingLogin : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
        supportActionBar?.hide()
        viewBinding()
        val prefs = LoginSessionPreferences(this)
        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        val mainViewModel: MainViewModel by viewModels {
            factory
        }
        checkSessionOnStart(prefs)
        mainViewModel.responseLogin.observe(this){
            if (it.body() != null && !prefs.checkLoginSession() ) {
                    Toast.makeText(this, "Success Login", Toast.LENGTH_SHORT).show()
                    intentToStoriesActivity()
            }
            else Toast.makeText(this, "Failed Login", Toast.LENGTH_SHORT).show()
            setLoadingProgress(false)
        }
        mainViewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        enableButtonLogin()
        wrapEspressoIdlingResource {
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    enableButtonLogin()
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    enableButtonLogin()
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
            loginButton.setOnClickListener {
                val flagPass = passwordEditText.error
                val flagEmail = emailEditText.error
                if (loginButton.isEnabled){
                    if (flagPass.isNullOrBlank() && flagEmail.isNullOrBlank()) {
                        mainViewModel.loginUser(
                            emailEditText.text.toString(),
                            passwordEditText.text.toString()
                        )
                        setLoadingProgress(true)
                    }
                    else{
                        Toast.makeText(this@MainActivity, "Email/Password Salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            daftarStringButton.setOnClickListener(this)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            daftarStringButton.id ->{
                val intentRegisterActivity = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intentRegisterActivity)
            }
        }
    }

    private fun enableButtonLogin() {
        val pass = passwordEditText.text
        val email = emailEditText.text
        loginButton.isEnabled = (pass != null && pass.toString().isNotEmpty() &&
                email != null && email.toString().isNotEmpty())
    }

    private fun intentToStoriesActivity(){
        val intentRegisterActivity = Intent(this@MainActivity, StoriesActivity::class.java)
        ActivityCompat.finishAffinity(this)
        startActivity(intentRegisterActivity)
    }

    private fun checkSessionOnStart(prefs: LoginSessionPreferences) {
        val sessionNotSet = prefs.checkLoginSession()
        if (!sessionNotSet){
            intentToStoriesActivity()
        }
    }

    private fun setLoadingProgress(isLoading : Boolean){
        if (isLoading){
            loginButton.apply {
                visibility = View.INVISIBLE
                isClickable = false
            }
            loadingLogin.visibility = View.VISIBLE
        } else {
            loginButton.apply {
                visibility = View.VISIBLE
                isClickable = true
            }
            loadingLogin.visibility = View.GONE
        }
    }

    private fun viewBinding(){
        passwordEditText = mainActivityBinding.passInsert
        emailEditText = mainActivityBinding.emailInsert
        loginButton = mainActivityBinding.loginButton
        daftarStringButton = mainActivityBinding.daftarStringButton
        loadingLogin = mainActivityBinding.progressBarLogin
    }
}

