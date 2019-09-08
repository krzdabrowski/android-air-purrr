package com.krzdabrowski.airpurrr.main.current

import android.content.Context
import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

// TODO: ROBOLECTRIC -> https://stackoverflow.com/questions/18008044/assert-imageview-was-loaded-with-specific-drawable-resource-id
class BaseModelTest {
    private lateinit var model: BaseModel

    @MockK
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        model = DetectorModel("", null)
    }

    @Test
    fun `test1`() {
        val dataPercentage = 25.0
    }

}