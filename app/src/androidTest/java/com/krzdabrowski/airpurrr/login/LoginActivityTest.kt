package com.krzdabrowski.airpurrr.login

import android.Manifest
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
import androidx.test.rule.GrantPermissionRule
import com.krzdabrowski.airpurrr.BuildConfig
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.common.EspressoIdlingResource
import com.krzdabrowski.airpurrr.utils.DataBindingIdlingResource
import com.krzdabrowski.airpurrr.utils.monitorActivity
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class LoginActivityTest {
    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var activityScenario: ActivityScenario<LoginActivity>

    // Permission rule to dismiss Location pop-up when launching Main screen
    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)!!

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

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun tearDown() {
        release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // region Other tests
    // ProgressBar & Espresso doesn't work well, hence no tests for showing ProgressBar
    @Test
    fun test1_givenWrongForm_whenClickOnManualLoginButton_thenProgressBarIsNotShown() {
        // Click on login form, type any credentials, and login
        onView(withId(R.id.input_email))
                .perform(typeText("any@login.com"), closeSoftKeyboard())
        onView(withId(R.id.btn_login))
                .perform(click())

        // Verify that performing a click will not show the progress bar on top
        onView(withId(R.id.progress_bar))
                .check(matches(not(isDisplayed())))
    }
    // endregion

    // region Navigation tests
    @Test
    fun test2_givenWrongCredentials_whenClickOnManualLoginButton_thenErrorSnackbarIsShown() {
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

    @Test
    fun test3_givenCorrectCredentials_whenClickOnManualLoginButton_thenAppNavigatesToMainScreen() {
        // Click on each form, type valid credentials, and login
        onView(withId(R.id.input_email))
                .perform(typeText(BuildConfig.TestLogin), closeSoftKeyboard())
        onView(withId(R.id.input_password))
                .perform(typeText(BuildConfig.TestPassword), closeSoftKeyboard())
        onView(withId(R.id.btn_login))
                .perform(click())

        // Verify that performing a click will show Main screen
        onView(withId(R.id.tab_layout))
                .check(matches(isDisplayed()))
    }

    // no idea how to test Biometric class yet
    // endregion
}