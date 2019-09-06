package com.krzdabrowski.airpurrr.main

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.login.LoginActivity
import com.krzdabrowski.airpurrr.settings.SettingsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    // Permission rule to dismiss Location pop-up when launching Main screen
    @get:Rule
    val permissionRule = GrantPermissionRule.grant(ACCESS_FINE_LOCATION)!!

    @Before
    fun setUp() {
        init()

        // Start up Main screen
        activityScenario = launchActivity()
    }

    @Test
    fun clickOnBackButton_DoesntOpenLoginScreen() {
        // Click on a back button
        pressBackUnconditionally()

        // Verify that app doesn't go back to LoginActivity
        intended(hasComponent(LoginActivity::class.java.name), times(0))
    }

    @Test
    fun clickOnMenuButton_OpensSettingsScreen() {
        // Click on a settings button
        onView(withId(R.id.menu_settings))
            .perform(click())

        // Verify that app goes to Settings screen
        intended(hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun clickOnMenuButton_ThenBackButton_OpensMainScreen() {
        // Click on a settings button
        onView(withId(R.id.menu_settings))
                .perform(click())

        // Click on a back button
        onView(withContentDescription(R.string.abc_action_bar_up_description))
                .perform(click())

        // Verify that app goes back to Main screen
        intended(hasComponent(MainActivity::class.java.name))
    }


    @Test
    fun clickOnMenuButton_ThenBackButtonTwice_DoesntOpenLoginScreen() {
        // Click on a settings button
        onView(withId(R.id.menu_settings))
                .perform(click())

        // Click on a back button twice
        pressBackUnconditionally()
        pressBackUnconditionally()

        intended(hasComponent(LoginActivity::class.java.name), times(0))
    }

    @After
    fun tearDown() {
        release()
    }
}