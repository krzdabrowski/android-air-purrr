package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.krzdabrowski.airpurrr.databinding.FragmentDataCurrentBinding
import com.krzdabrowski.airpurrr.viewmodel.ApiViewModel
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_data_current.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class DataCurrentFragment : Fragment() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel(from = { parentFragment!! })
    private val apiViewModel: ApiViewModel by sharedViewModel(from = { parentFragment!! })
    private lateinit var binding: FragmentDataCurrentBinding

    private fun getDetectorData() = detectorViewModel.getLiveData().observe(this, Observer { value -> binding.detectorData = value })
    private fun getApiData() = apiViewModel.getLiveData().observe(this, Observer { value -> binding.apiData = value })
    private fun getApi() = apiViewModel.userLocation.observe(this, Observer { location ->
        if (location != null) {
            getApiData()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDataCurrentBinding.inflate(inflater, container, false)
        binding.flagDetectorApi = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partial_main_data_pm25.setOnClickListener { onDataClick() }
        partial_main_data_pm10.setOnClickListener { onDataClick() }
        swipe_refresh.setOnRefreshListener { onRefresh() }

        automaticDownload()
    }

    private fun onDataClick() {
        binding.flagDetectorApi = !binding.flagDetectorApi!!
    }

    private fun onRefresh() {
        getApi()
        getDetectorData()
        swipe_refresh.isRefreshing = false
    }

    private fun automaticDownload() {
        val timer = Timer()
        val minuteTask = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    getApi()
                    getDetectorData()
                }
            }
        }
        timer.schedule(minuteTask, 0, (1000 * 60 * 2).toLong())  // 1000*60*2 every 2 minutes
    }
}