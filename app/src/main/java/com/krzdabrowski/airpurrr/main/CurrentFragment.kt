package com.krzdabrowski.airpurrr.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.krzdabrowski.airpurrr.databinding.FragmentCurrentBinding
import com.krzdabrowski.airpurrr.main.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CurrentFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentCurrentBinding
    private val detectorViewModel: DetectorViewModel by sharedViewModel(from = { parentFragment!!.activity!! })
    private val apiViewModel: ApiViewModel by sharedViewModel(from = { parentFragment!! })
    private val baseViewModel: BaseViewModel by sharedViewModel(from = { parentFragment!! })
    private var isRefreshing = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCurrentBinding.inflate(inflater, container, false)
        binding.baseVm = baseViewModel
        binding.refreshListener = this
        binding.isRefreshing = isRefreshing

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchNewData()
    }

    private fun getDetectorData() = detectorViewModel.currentValuesLiveData.observe(viewLifecycleOwner) { value -> binding.detectorData = value }

    private fun getApiData() = apiViewModel.liveData.observe(viewLifecycleOwner) { value -> binding.apiData = value.first }

    private fun getLocation() = apiViewModel.userLocation.observe(viewLifecycleOwner) { getApiData() }

    override fun onRefresh() = fetchNewData()

    private fun fetchNewData() {
        getDetectorData()
        getLocation()
        binding.isRefreshing = false
    }
}