package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidintermediate_sub1_wildanfajrialfarabi.DataDummy
import com.example.androidintermediate_sub1_wildanfajrialfarabi.TestRule
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.RetrofitApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private var testRule = TestRule()
    private val dataDummySuccessLogin = DataDummy.generateSuccessLogin()
    private val dataDummyFailedLogin = DataDummy.generateFailedLogin()
    private val dataDummySuccessRegister = DataDummy.generateSuccessRegister()
    private lateinit var apiService: RetrofitApiService

    @Before
    fun setup(){
        apiService = FakeApiService()
    }

    @Test
    fun `user login success and return no error`(){
        testRule.runBlockingTest {
            val email = "valid_email@gmail.com"
            val password = "valid_password123"
            val expectedStories = dataDummySuccessLogin
            val actualStories = apiService.loginUser(email,password)
            assertNotNull(actualStories.body()?.loginResult!!.token)
            assertEquals(expectedStories.body(), actualStories.body())
        }
    }

    @Test
    fun `user login failed and return error`(){
        testRule.runBlockingTest {
            val email = ""
            val password = ""
            val expectedStories = dataDummyFailedLogin
            val actualStories = apiService.loginUser(email,password)
            assertNull(actualStories.body())
            assertEquals(expectedStories.body(), actualStories.body())
        }
    }

    @Test
    fun `user register success and return body`(){
        testRule.runBlockingTest {
            val name = "nama_valid"
            val email = "valid_email@gmail.com"
            val password = "valid_password123"
            val expectedResponse = dataDummySuccessRegister
            val actualResponse = apiService.registerUser(name,email,password)
            assertNotNull(actualResponse.body())
            assertEquals(expectedResponse.body(), actualResponse.body())
        }
    }

    @Test
    fun `user register failed and return null body`(){
        testRule.runBlockingTest {
            val name = ""
            val email = "notvalid@notvalid.com"
            val password = "notValid"
            val expectedResponse = dataDummyFailedLogin
            val actualResponse = apiService.registerUser(name,email,password)
            assertNull(actualResponse.body())
            assertEquals(expectedResponse.body(), actualResponse.body())
        }
    }
}