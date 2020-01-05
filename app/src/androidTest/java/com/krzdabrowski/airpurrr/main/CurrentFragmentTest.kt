package com.krzdabrowski.airpurrr.main

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.core.MainActivity
import com.krzdabrowski.airpurrr.utils.DataBindingIdlingResource
import com.krzdabrowski.airpurrr.utils.monitorActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CurrentFragmentTest {
    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    // Permission rule to dismiss Location pop-up when launching Main screen
    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)!!

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

        activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun so_whenClickOnDetectorPm25Tile_ThenApiDataTilesIsShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_current_pm25))
            .perform(click())

        // Verify that API data tile is shown
        onView(allOf(
                withId(R.id.ic_airly_logo),
                hasSibling(withText(R.string.main_data_info_pm25))
        ))
            .check(matches(isDisplayed()))
    }

    @Test
    fun so_whenClickOnDetectorPm10Tile_ThenApiDataTilesIsShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_current_pm10))
                .perform(click())

        // Verify that API data tile is shown
        onView(allOf(
                withId(R.id.ic_airly_logo),
                hasSibling(withText(R.string.main_data_info_pm10))
        ))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnDetectorPm25Tile_andOnApiPm25Tile_thenDetectorDataTilesIsAgainShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_current_pm25))
                .perform(click())

        // Click on API data tile
        onView(withId(R.id.partial_current_pm25))
                .perform(click())

        // Verify that detector data tile is shown
        onView(allOf(
                withText(R.string.main_data_info_indoors),
                hasSibling(withText(R.string.main_data_info_pm25))
        ))
                .check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnDetectorPm10Tile_andOnApiPm10Tile_thenDetectorDataTilesIsAgainShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_current_pm10))
                .perform(click())

        // Click on API data tile
        onView(withId(R.id.partial_current_pm10))
                .perform(click())

        // Verify that detector data tile is shown
        onView(allOf(
                withText(R.string.main_data_info_indoors),
                hasSibling(withText(R.string.main_data_info_pm10))
        ))
                .check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnDetectorPm25Tile_andOnApiPm10Tile_thenDetectorDataTilesIsAgainShown() {
        // Click on detector PM2.5 data tile
        onView(withId(R.id.partial_current_pm25))
                .perform(click())

        // Click on API PM10 data tile
        onView(withId(R.id.partial_current_pm10))
                .perform(click())

        // Verify that both tiles are detector ones again
        onView(allOf(
                withText(R.string.main_data_info_indoors),
                hasSibling(withText(R.string.main_data_info_pm25))
        ))
                .check(matches(isDisplayed()))

        onView(allOf(
                withText(R.string.main_data_info_indoors),
                hasSibling(withText(R.string.main_data_info_pm10))
        ))
                .check(matches(isDisplayed()))
    }
}