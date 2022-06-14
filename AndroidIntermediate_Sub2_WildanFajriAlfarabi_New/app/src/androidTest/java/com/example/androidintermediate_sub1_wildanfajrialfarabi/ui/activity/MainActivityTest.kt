package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest{

    private val dummyPass= "123456"
    private val dummyEmail = "awa@gmail.com"
    private val dummyPassInvalid= " "
    private val dummyEmailInvalid = " "

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun insertValidEmailAndPassThenLoginButtonCanBeClicked() {
        onView(withId(R.id.pass_insert)).perform(
            typeText(dummyPass), ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.email_insert)).perform(
            typeText(dummyEmail), ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.login_button)).check(matches(isClickable()))
    }

    @Test
    fun insertInvalidEmailAndPassThenLoginButtonCannotBeClicked() {
        onView(withId(R.id.pass_insert)).perform(
            typeText(dummyPassInvalid), ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.email_insert)).perform(
            typeText(dummyEmailInvalid), ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.login_button)).check(matches(isNotEnabled()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}