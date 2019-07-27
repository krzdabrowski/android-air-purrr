package com.krzdabrowski.airpurrr.main.current

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.krzdabrowski.airpurrr.databinding.FragmentDataCurrentBinding
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DataCurrentFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentDataCurrentBinding

    private val detectorViewModel: DetectorViewModel by sharedViewModel(from = { parentFragment!!.activity!! })
    private val apiViewModel: ApiViewModel by sharedViewModel(from = { parentFragment!! })
    private val baseViewModel: BaseViewModel by viewModel()

    private var isRefreshing = false
    private val fetchingInterval: Long = 1000 * 60 * 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDataCurrentBinding.inflate(inflater, container, false)
        binding.baseVm = baseViewModel
        binding.refreshListener = this
        binding.isRefreshing = isRefreshing

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchNewData()
    }

    private fun getDetectorData() = detectorViewModel.getLiveData().observe(this, Observer { value -> binding.detectorData = value })

    private fun getApiData() = apiViewModel.getLiveData().observe(this, Observer { value -> binding.apiData = value })

    private fun getApi() = apiViewModel.userLocation.observe(this, Observer { location ->
        if (location != null) {
            getApiData()
            runPeriodicFetching()
        }
    })

    override fun onRefresh() = fetchNewData()

    private fun fetchNewData() {
        getDetectorData()
        getApi()
        binding.isRefreshing = false
    }

    private fun runPeriodicFetching() {
        val handler = Handler()
        handler.postDelayed(runnable {
            fetchNewData()
            handler.postDelayed(this, fetchingInterval)
        }, fetchingInterval)
    }

    private inline fun runnable(crossinline runnableRun: Runnable.() -> Unit) = object : Runnable {
        override fun run() = runnableRun()
    }
}