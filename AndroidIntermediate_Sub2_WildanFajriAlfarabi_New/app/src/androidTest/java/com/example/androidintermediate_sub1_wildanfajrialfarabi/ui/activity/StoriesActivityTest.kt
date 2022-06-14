package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.androidintermediate_sub1_wildanfajrialfarabi.JsonConvert
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.retrofit.APIConfig
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class StoriesActivityTest {

    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(StoriesActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(StoriesActivity::class.java)
        mockWebServer.start(8080)
        APIConfig.baseURL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStories_Success() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConvert.readStringFromFile("success_get_story.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.story_recview)).check(matches(isDisplayed()))
    }

    @Test
    fun getStories_Failed() {

        val mockResponse = MockResponse()
            .setResponseCode(400)
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.story_recview)).check(matches(isNotFocused()))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}