package com.krzdabrowski.airpurrr

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.krzdabrowski.airpurrr.common.EspressoIdlingResource
import com.krzdabrowski.airpurrr.login.LoginActivity
import com.krzdabrowski.airpurrr.utils.DataBindingIdlingResource
import com.krzdabrowski.airpurrr.utils.monitorActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var activityScenario: ActivityScenario<LoginActivity>

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
        init()

        // Start up Login screen
        activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)
    }

    @Test
    fun loginManual_positivePath() {
        // Click on each form, type valid credentials, and login
        onView(withId(R.id.input_email))
                .perform(typeText(BuildConfig.TestLogin), closeSoftKeyboard())
        onView(withId(R.id.input_password))
                .perform(typeText(BuildConfig.TestPassword), closeSoftKeyboard())
        onView(withId(R.id.btn_login))
                .perform(click())

        // Verify that performing a click navigates to Main screen
        // verify { navController.navigate(LoginFragmentDirections.navigateToMainScreen()) }
    }

    @Test
    fun loginManual_negativePath() {
        // Click on each form, type invalid credentials, and login
        onView(withId(R.id.input_email))
                .perform(typeText("wrong@login.com"), closeSoftKeyboard())
        onView(withId(R.id.input_password))
                .perform(typeText("wrongpassword"), closeSoftKeyboard())
        onView(withId(R.id.btn_login))
                .perform(click())

        // Verify that performing a click will show an "Authentication error!" snackbar
        onView(withText(R.string.login_error_auth))
                .check(matches(isDisplayed()))
    }

    // no idea how to test Biometric class yet

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        release()
        stopKoin()
    }
}