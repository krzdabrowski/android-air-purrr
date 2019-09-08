package com.krzdabrowski.airpurrr.main

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.DataCurrentFragment
import com.krzdabrowski.airpurrr.utils.DataBindingIdlingResource
import com.krzdabrowski.airpurrr.utils.monitorFragment
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
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
    fun clickOnDetectorPm25Tile_showsApiDataTiles() {
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
    fun clickOnDetectorPm10Tile_showsApiDataTiles() {
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
    fun clickOnDetectorPm25Tile_thenOnApiPm25Tile_goesBackToDetectorDataTiles() {
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
    fun clickOnDetectorPm10Tile_thenOnApiPm10Tile_goesBackToDetectorDataTiles() {
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
    fun clickOnDetectorPm25Tile_thenOnApiPm10Tile_goesBackToDetectorDataTiles() {
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

    // TODO: ROBOLECTRIC? -> doesn't work like that, might need more integration test (data/logic vs Espresso swipeDown())?
    @Test
    fun test1() {
        // Swipe down to refresh
        onView(withId(R.id.swipe_refresh))
            .perform(swipeDown())

        // Verify if it's refreshing
        onView(withId(R.id.swipe_refresh))
            .check(matches(SwipeRefreshLayoutMatchers.isRefreshing()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    object SwipeRefreshLayoutMatchers {
        @JvmStatic
        fun isRefreshing(): Matcher<View> {
            return object : BoundedMatcher<View, SwipeRefreshLayout>(
                    SwipeRefreshLayout::class.java) {

                override fun describeTo(description: Description) {
                    description.appendText("is refreshing")
                }

                override fun matchesSafely(view: SwipeRefreshLayout): Boolean {
                    return view.isRefreshing
                }
            }
        }
    }
}