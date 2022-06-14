package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityRegisterBinding
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.MainViewModel
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.ViewModelFactory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview.InsertEmailText
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview.InsertPasswordText
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.customview.LoginButton

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var emailEditText: InsertEmailText
    private lateinit var passwordEditText: InsertPasswordText
    private lateinit var nameEditText: EditText
    private lateinit var regisButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        supportActionBar?.hide()
        viewBinding()

        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        val mainViewModel: MainViewModel by viewModels {
            factory
        }
        mainViewModel.responseRegister.observe(this){
            if (it.body() != null){
                Toast.makeText(this, "Register success!", Toast.LENGTH_SHORT).show()
                val intentMainActivity = Intent(this, MainActivity::class.java)
                ActivityCompat.finishAffinity(this)
                startActivity(intentMainActivity)
            } else {
                Toast.makeText(this, "Failed to register!", Toast.LENGTH_SHORT).show()
            }
        }
        mainViewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        enableButtonLogin()

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
        regisButton.setOnClickListener {
            if (regisButton.isEnabled){
                registrationLogic(mainViewModel)
            }
        }
    }

    private fun registrationLogic(mainViewModel: MainViewModel){
        val flagPass = checkPass()
        val flagEmail = checkEmail()
        if (flagEmail && flagPass) {
            mainViewModel.registerUser(
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
                nameEditText.text.toString())
            Toast.makeText(this, "Berhasil Submit", Toast.LENGTH_SHORT).show()
        }
        else if (!flagPass && !flagEmail){
            passwordEditText.error = "Panjang password minimal 6"
            emailEditText.error = "Alamat email salah"
            Toast.makeText(this, "Email/Password Salah", Toast.LENGTH_SHORT).show()
        }
        else if (!flagPass){
            passwordEditText.error = "Panjang password minimal 6"
            Toast.makeText(this, "Email/Password Salah", Toast.LENGTH_SHORT).show()
        }
        else {
            emailEditText.error = "Alamat email salah"
            Toast.makeText(this, "Email/Password Salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableButtonLogin() {
        val pass = passwordEditText.text
        val email = emailEditText.text
        regisButton.isEnabled = (pass != null && pass.toString().isNotEmpty() &&
                email != null && email.toString().isNotEmpty())
    }

    private fun checkPass(): Boolean{
        return (passwordEditText.text.toString().length >= 6)
    }

    private fun checkEmail(): Boolean{
        val strEmail = emailEditText.text.toString() as CharSequence
        return Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()
    }

    private fun viewBinding(){
        passwordEditText = registerBinding.registerPass
        emailEditText = registerBinding.registerEmail
        regisButton = registerBinding.regisButton
        nameEditText = registerBinding.registerName
    }
}
