package com.krzdabrowski.airpurrr.main

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.DataCurrentFragment
import com.krzdabrowski.airpurrr.utils.DataBindingIdlingResource
import com.krzdabrowski.airpurrr.utils.monitorFragment
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class DataCurrentFragmentTest {
    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var fragmentScenario: FragmentScenario<DataCurrentFragment>

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

        fragmentScenario = launchFragmentInContainer(null, R.style.Main_AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)
    }

    @Test
    fun whenClickOnDetectorPm25Tile_ThenApiDataTilesIsShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_main_data_pm25))
            .perform(click())

        // Verify that API data tile is shown
        onView(allOf(
                withText(R.string.main_data_info_api),
                hasSibling(withText(R.string.main_data_info_pm25))
        ))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnDetectorPm10Tile_ThenApiDataTilesIsShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_main_data_pm10))
                .perform(click())

        // Verify that API data tile is shown
        onView(allOf(
                withText(R.string.main_data_info_api),
                hasSibling(withText(R.string.main_data_info_pm10))
        ))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnDetectorPm25Tile_andOnApiPm25Tile_thenDetectorDataTilesIsAgainShown() {
        // Click on detector data tile
        onView(withId(R.id.partial_main_data_pm25))
                .perform(click())

        // Click on API data tile
        onView(withId(R.id.partial_main_data_pm25))
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
        onView(withId(R.id.partial_main_data_pm10))
                .perform(click())

        // Click on API data tile
        onView(withId(R.id.partial_main_data_pm10))
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
        onView(withId(R.id.partial_main_data_pm25))
                .perform(click())

        // Click on API PM10 data tile
        onView(withId(R.id.partial_main_data_pm10))
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

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
}