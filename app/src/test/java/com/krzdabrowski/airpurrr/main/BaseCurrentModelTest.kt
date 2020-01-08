package com.krzdabrowski.airpurrr.main

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.core.MainActivity
import com.krzdabrowski.airpurrr.main.detector.DetectorCurrentModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.LooperMode.Mode.PAUSED

@RunWith(RobolectricTestRunner::class)
@LooperMode(PAUSED)
class BaseCurrentModelTest {
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    private lateinit var model: BaseCurrentModel

    @Before
    fun setUp() {
        activityScenario = launchActivity()
        model = DetectorCurrentModel("", DetectorCurrentModel.Data(0.0, 0.0))
    }

    @Test
    fun `given no data, then data tile is gray color`() {
        val dataPercentage = 0.0

        val drawableUnderTest = model.getBackgroundColorDrawable(InstrumentationRegistry.getInstrumentation().context, dataPercentage)

        assertThat(shadowOf(drawableUnderTest).createdFromResId).isEqualTo(R.drawable.data_unavailable)
    }

    @Test
    fun `given low percentage of pollution, then data tile is green color`() {
        val dataPercentage = 25.0

        val drawableUnderTest = model.getBackgroundColorDrawable(InstrumentationRegistry.getInstrumentation().context, dataPercentage)

        assertThat(shadowOf(drawableUnderTest).createdFromResId).isEqualTo(R.drawable.data_green)
    }

    @Test
    fun `given moderate percentage of pollution, then data tile is lime color`() {
        val dataPercentage = 75.0

        val drawableUnderTest = model.getBackgroundColorDrawable(InstrumentationRegistry.getInstrumentation().context, dataPercentage)

        assertThat(shadowOf(drawableUnderTest).createdFromResId).isEqualTo(R.drawable.data_lime)
    }

    @Test
    fun `given medium percentage of pollution, then data tile is yellow color`() {
        val dataPercentage = 150.0

        val drawableUnderTest = model.getBackgroundColorDrawable(InstrumentationRegistry.getInstrumentation().context, dataPercentage)

        assertThat(shadowOf(drawableUnderTest).createdFromResId).isEqualTo(R.drawable.data_yellow)
    }

    @Test
    fun `given high percentage of pollution, then data tile is yellow red`() {
        val dataPercentage = 300.0

        val drawableUnderTest = model.getBackgroundColorDrawable(InstrumentationRegistry.getInstrumentation().context, dataPercentage)

        assertThat(shadowOf(drawableUnderTest).createdFromResId).isEqualTo(R.drawable.data_red)
    }

}