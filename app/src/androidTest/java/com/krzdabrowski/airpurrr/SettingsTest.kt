package com.krzdabrowski.airpurrr

import android.Manifest
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsTest {
//    private lateinit var fragmentScenario: FragmentScenario<MainFragment>

    // Permission rule to dismiss Location pop-up at the start
    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)!!

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun setUp() {
        init()
//        fragmentScenario = launchFragmentInContainer(null, R.style.Main_AppTheme)
    }

    @Test
    fun clickOnBackButton_OpensMainScreen() {

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