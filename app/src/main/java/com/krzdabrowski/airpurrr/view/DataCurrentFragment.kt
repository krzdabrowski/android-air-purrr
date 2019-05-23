package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.*
import com.krzdabrowski.airpurrr.databinding.FragmentDataCurrentBinding
import com.krzdabrowski.airpurrr.model.ApiModel
import com.krzdabrowski.airpurrr.utils.DataCurrentDownloadWorker
import com.krzdabrowski.airpurrr.utils.WORKER_KEY_API_RESULT
import com.krzdabrowski.airpurrr.utils.WORKER_KEY_LOCATION_DATA
import com.krzdabrowski.airpurrr.utils.WORKER_TAG_API_PERIODIC_REQUEST
import com.krzdabrowski.airpurrr.viewmodel.ApiViewModel
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_data_current.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class DataCurrentFragment : Fragment() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel(from = { parentFragment!! })
    private val apiViewModel: ApiViewModel by sharedViewModel(from = { parentFragment!! })
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var binding: FragmentDataCurrentBinding

    private fun getDetectorData() = detectorViewModel.getLiveData().observe(this, Observer { value -> binding.detectorData = value })
    private fun getApiData() = apiViewModel.getLiveData().observe(this, Observer { value -> binding.apiData = value })
    private fun getApi() = apiViewModel.userLocation.observe(this, Observer { location ->
        if (location != null) {
            getApiData()
            runPeriodicFetching()
        }
    })

    private fun getPeriodicData() {
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this, Observer { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                val data = workInfo.outputData.getDoubleArray(WORKER_KEY_API_RESULT)
                binding.apiData = ApiModel(null, data!!) }
        })
    }

    fun runPeriodicFetching() {
        val locationArray = doubleArrayOf(apiViewModel.userLocation.value!!.latitude, apiViewModel.userLocation.value!!.longitude)
        val locationData = workDataOf(WORKER_KEY_LOCATION_DATA to locationArray)

        periodicWorkRequest = PeriodicWorkRequestBuilder<DataCurrentDownloadWorker>(15, TimeUnit.MINUTES).addTag(WORKER_TAG_API_PERIODIC_REQUEST)
                .setInputData(locationData)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(WORKER_TAG_API_PERIODIC_REQUEST, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest)

        getPeriodicData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDataCurrentBinding.inflate(inflater, container, false)
        binding.flagDetectorApi = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partial_main_data_pm25.setOnClickListener { onDataClick() }
        partial_main_data_pm10.setOnClickListener { onDataClick() }
        swipe_refresh.setOnRefreshListener { fetchNewData() }

        fetchNewData()
    }

    private fun onDataClick() {
        binding.flagDetectorApi = !binding.flagDetectorApi!!
    }

    private fun fetchNewData() {
        getDetectorData()
        getApi()
        swipe_refresh.isRefreshing = false
    }
}