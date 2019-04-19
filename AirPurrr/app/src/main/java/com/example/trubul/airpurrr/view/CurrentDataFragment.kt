package com.example.trubul.airpurrr.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.databinding.FragmentCurrentDataBinding
import com.example.trubul.airpurrr.viewmodel.ApiViewModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_current_data.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CurrentDataFragment : Fragment() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private lateinit var binding: FragmentCurrentDataBinding

    private fun getDetectorData() = detectorViewModel.getLiveData().observe(this, Observer { value -> binding.detectorData = value })
    private fun getApiData() = apiViewModel.getLiveData().observe(this, Observer { value -> binding.apiData = value })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCurrentDataBinding.inflate(inflater, container, false)
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
        getApiData()
        getDetectorData()
        swipe_refresh.isRefreshing = false
    }

    private fun automaticDownload() {
        val timer = Timer()
        val minuteTask = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    getApiData()
                    getDetectorData()
                }
            }
        }
        timer.schedule(minuteTask, 0, (1000 * 60).toLong())  // 1000*60*1 every 1 minute
    }
}