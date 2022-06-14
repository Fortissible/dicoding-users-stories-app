package com.example.androidintermediate_sub1_wildanfajrialfarabi.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidintermediate_sub1_wildanfajrialfarabi.DataDummy
import com.example.androidintermediate_sub1_wildanfajrialfarabi.TestRule
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.local.StoriesDao
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.RetrofitApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StoriesRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private var testRule = TestRule()
    private val dataDummy = DataDummy.generateDummyStories()
    private lateinit var apiService: RetrofitApiService
    private lateinit var storiesDao : StoriesDao

    @Before
    fun setup(){
        apiService = FakeApiService()
        storiesDao = FakeStoriesDao()
    }

    @Test
    fun `get stories from api success and should return non-null list stories`() = testRule.runBlockingTest{
        val token = "valid token"
        val location = 1
        val page = 1
        val size = 10
        val expectedStories = dataDummy
        val actualStories = apiService.getStoryWithLoc(token,location,page,size)
        Assert.assertNotNull(actualStories)
        Assert.assertEquals(expectedStories.size, actualStories.listStory.size)
    }

    @Test
    fun `get stories from api failed because token not available in db should return empty list and error`() =
        testRule.runBlockingTest{
        val token = "not valid token"
        val location = 1
        val page = 1
        val size = 10
        val expectedError = true
        val expectedStoriesSize = 0
        val actualStories = apiService.getStoryWithLoc(token,location,page,size)
        Assert.assertTrue(actualStories.error)
        Assert.assertEquals(expectedError,actualStories.error)
        Assert.assertEquals(expectedStoriesSize,actualStories.listStory.size)
    }

    @Test
    fun `get stories from db success`() = testRule.runBlockingTest{
        val actualStories = storiesDao.getListStoryItem()
        val expectedStories = dataDummy
        Assert.assertEquals(expectedStories,actualStories)
        Assert.assertNotNull(actualStories.size)
    }
}