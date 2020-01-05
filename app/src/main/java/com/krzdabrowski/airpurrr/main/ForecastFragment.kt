package com.krzdabrowski.airpurrr.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.db.williamchart.data.Scale
import com.krzdabrowski.airpurrr.databinding.FragmentForecastBinding
import com.krzdabrowski.airpurrr.main.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_forecast.*
import kotlinx.android.synthetic.main.partial_forecast_data.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.math.roundToInt

class ForecastFragment : Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private val detectorViewModel: DetectorViewModel by sharedViewModel(from = { parentFragment!!.activity!! })
    private val apiViewModel: ApiViewModel by sharedViewModel(from = { parentFragment!! })
    private val baseViewModel: BaseViewModel by sharedViewModel(from = { parentFragment!! })

    val lineSet = linkedMapOf(
            "10:00" to 5f,
            "11:00" to 4.5f,
            "12:00" to 4.7f,
            "13:00" to 3.5f,
            "14:00" to 3.6f,
            "15:00" to 7.5f
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentForecastBinding.inflate(inflater, container, false)
        binding.baseVm = baseViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        getDetectorData()
//        getLocation()

        formatCharts()
        formatChart()
    }

    private fun getDetectorData() = detectorViewModel.liveData.observe(viewLifecycleOwner) { value -> binding.detectorData = value }

    private fun getApiData() = apiViewModel.liveData.observe(viewLifecycleOwner) {  }

    private fun getLocation() = apiViewModel.userLocation.observe(viewLifecycleOwner) { getApiData() }

    private fun formatCharts() {
        with (partial_forecast_pm25.chart, partial_forecast_pm10.chart) {
            labelsFormatter = { "${it.roundToInt()}%" }
            animation.duration = 1000L
            animate(lineSet)
        }
    }

    private fun formatChart() {
        with (partial_forecast_pm25.chart) {
            gradientFillColors = intArrayOf(Color.parseColor("#FFFFFF"), Color.TRANSPARENT)
            scale = Scale(0f, 12f)
        }

        with (partial_forecast_pm10.chart) {
            gradientFillColors = intArrayOf(Color.parseColor("#FFFFFF"), Color.TRANSPARENT)
            scale = Scale(0f, 12f)
        }
    }

    inline fun <T> with(vararg receivers: T, block: T.() -> Unit) {
        for (receiver in receivers) receiver.block()
    }
}