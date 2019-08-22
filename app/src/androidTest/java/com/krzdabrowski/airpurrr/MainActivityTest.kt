package com.krzdabrowski.airpurrr

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.krzdabrowski.airpurrr.login.LoginActivity
import com.krzdabrowski.airpurrr.main.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    // Permission rule to dismiss Location pop-up at the start
    @get:Rule val permissionRule = GrantPermissionRule.grant(ACCESS_FINE_LOCATION)!!

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
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

        // Verify that MainActivity is destroyed and app doesn't go back to LoginActivity
        intended(not(hasComponent(LoginActivity::class.java.name)))
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun tearDown() {
        release()
        stopKoin()
    }
}