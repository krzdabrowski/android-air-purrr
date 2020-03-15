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
import com.krzdabrowski.airpurrr.main.core.MainFragment
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_forecast.*
import kotlinx.android.synthetic.main.partial_forecast_data.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.math.roundToInt

private const val NUMBER_OF_FORECAST_DATA_TO_SHOW = 8

class ForecastFragment : Fragment(), BaseViewModel.OnForecastCallback, MainFragment.ViewPagerRefreshListener {
    private lateinit var binding: FragmentForecastBinding
    private val detectorViewModel: DetectorViewModel by sharedViewModel(from = { requireParentFragment().requireActivity() })
    private val apiViewModel: ApiViewModel by sharedViewModel(from = { requireParentFragment() })
    private val baseViewModel: BaseViewModel by sharedViewModel(from = { requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentForecastBinding.inflate(inflater, container, false)
        baseViewModel.forecastClickCallback = this
        (parentFragment as MainFragment).forecastRefreshListener = this
        binding.baseVm = baseViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDetectorData()
        getLocation()
    }

    private fun getDetectorData() = detectorViewModel.forecastValuesLiveData.observe(viewLifecycleOwner) { value ->
        binding.detectorData = value
        onRefresh()
    }

    private fun getApiData() = apiViewModel.liveData.observe(viewLifecycleOwner) { value ->
        binding.apiData = value.second
        onRefresh()
    }

    private fun getLocation() = apiViewModel.userLocation.observe(viewLifecycleOwner) { getApiData() }

    private fun formatPm25Chart(forecastModel: BaseForecastModel) {
        with (partial_forecast_pm25.chart) {
            val hoursToShow = forecastModel.result.hours.take(NUMBER_OF_FORECAST_DATA_TO_SHOW)
            val valuesToShow = forecastModel.result.pm25.take(NUMBER_OF_FORECAST_DATA_TO_SHOW)
            val mean = forecastModel.result.pm25.average()
            val max = forecastModel.result.pm25.maxBy { it }
            val mapToShow = LinkedHashMap(hoursToShow.zip(valuesToShow).toMap())

            gradientFillColors = intArrayOf(
                    forecastModel.getBackgroundColorInt(requireContext(), mean),
                    Color.TRANSPARENT
            )
            scale = Scale(0f, max?.times(1.5f) ?: 100f)
            labelsFormatter = { "${it.roundToInt()}%" }
            animate(mapToShow)
        }
    }

    private fun formatPm10Chart(forecastModel: BaseForecastModel) {
        with(partial_forecast_pm10.chart) {
            val hoursToShow = forecastModel.result.hours.take(NUMBER_OF_FORECAST_DATA_TO_SHOW)
            val valuesToShow = forecastModel.result.pm10.take(NUMBER_OF_FORECAST_DATA_TO_SHOW)
            val mean = forecastModel.result.pm10.average()
            val max = forecastModel.result.pm10.maxBy { it }
            val mapToShow = LinkedHashMap(hoursToShow.zip(valuesToShow).toMap())

            gradientFillColors = intArrayOf(
                    forecastModel.getBackgroundColorInt(requireContext(), mean),
                    Color.TRANSPARENT
            )
            scale = Scale(0f, max?.times(1.5f) ?: 100f)
            labelsFormatter = { "${it.roundToInt()}%" }
            animate(mapToShow)
        }
    }

    override fun onRefresh() {
        if (binding.detectorData == null || binding.apiData == null)
            return

        if (baseViewModel.flagDetectorApi.get()) {
            formatPm25Chart(binding.apiData as BaseForecastModel)
            formatPm10Chart(binding.apiData as BaseForecastModel)
        } else {
            formatPm25Chart(binding.detectorData as BaseForecastModel)
            formatPm10Chart(binding.detectorData as BaseForecastModel)
        }
    }

    override fun onForecastRefresh() {
        onRefresh()
    }
}