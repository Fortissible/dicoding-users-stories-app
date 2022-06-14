package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.Result
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.ErrorException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private var mainViewModel : MainViewModel = mock(MainViewModel::class.java)

    @Test
    fun `when clear login preferences should success`(){
        val expectedResult = Result.Success(true)
        `when`(mainViewModel.clearLoginPreferences()).thenReturn(expectedResult)
        val actualResult = mainViewModel.clearLoginPreferences()
        Assert.assertNotNull(actualResult)
        Assert.assertEquals(actualResult,expectedResult)
    }

    @Test
    fun `when login is success should not error`(){
        val emailDummy = "testingUnit@gmail.com"
        val passDummy = "123456"
        mainViewModel.loginUser(emailDummy,passDummy)
        Assert.assertNull(mainViewModel.errorMessage)
    }

    @Test
    fun `when login is failed should error`(){
        try {
            val emailDummy = "testingUnit@gmail.com"
            val passDummy = "errorpass"
            val expectedResult = ErrorException("Email/Password wrong")
            `when`(mainViewModel.loginUser(emailDummy,passDummy)).thenThrow(expectedResult)
            mainViewModel.loginUser(emailDummy,passDummy)
        } catch (e : ErrorException) {
            Assert.assertEquals(e.message.toString(),"Email/Password wrong")
            Assert.assertNotNull(e)
        }
    }

    @Test
    fun `when signup is success should not error`(){
        val nameDummy = "SiTesterGanteng"
        val emailDummy = "testingUnit@gmail.com"
        val passDummy = "123456"
        mainViewModel.registerUser(emailDummy,passDummy,nameDummy)
        Assert.assertNull(mainViewModel.errorMessage)
    }

    @Test
    fun `when signup is failed should error`(){
        try {
            val nameDummy = ""
            val emailDummy = "testingUnit@gmail.com"
            val passDummy = "123456"
            val expectedResult = ErrorException("Signup Failed")
            `when`(mainViewModel.registerUser(emailDummy,passDummy,nameDummy)).thenThrow(expectedResult)
            mainViewModel.registerUser(emailDummy,passDummy,nameDummy)
        } catch (e : ErrorException) {
            Assert.assertEquals(e.message.toString(),"Signup Failed")
            Assert.assertNotNull(e)
        }
    }
}