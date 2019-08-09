package com.krzdabrowski.airpurrr

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
import com.krzdabrowski.airpurrr.common.viewModelModule
import com.krzdabrowski.airpurrr.login.LoginFragment
import com.krzdabrowski.airpurrr.login.LoginFragmentDirections
import com.krzdabrowski.airpurrr.utils.DataBindingIdlingResource
import com.krzdabrowski.airpurrr.utils.monitorFragment
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Before
    fun setUp() {
        startKoin { modules(viewModelModule) }
        init()
    }

    @Test
    fun loginManual_positivePath() {
        @RelaxedMockK val mockNavController = mockk<NavController>()

        // Start up Login screen
        val fragmentScenario = launchFragmentInContainer<LoginFragment>(null, R.style.Login_AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        // Set mocked navigation controller and define its navigating behavior
        fragmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        every { mockNavController.navigate(LoginFragmentDirections.navigateToMainScreen()) } just Runs

        // Click on each form, type valid credentials, and login
        onView(withId(R.id.input_email))
                .perform(typeText(BuildConfig.TestLogin), closeSoftKeyboard())
        onView(withId(R.id.input_password))
                .perform(typeText(BuildConfig.TestPassword), closeSoftKeyboard())
        onView(withId(R.id.btn_login))
                .perform(click())

        // Verify that performing a click launches the correct Navigation action
        verify { mockNavController.navigate(LoginFragmentDirections.navigateToMainScreen()) }
    }

    @Test
    fun loginManual_negativePath() {
        // Start up Login screen
        val fragmentScenario = launchFragmentInContainer<LoginFragment>(null, R.style.Login_AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

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

    @After
    fun tearDown() {
        release()
        stopKoin()
    }
}