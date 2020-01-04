package com.krzdabrowski.airpurrr.main.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
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

    private fun getDetectorData() = detectorViewModel.liveData.observe(viewLifecycleOwner) { value -> binding.detectorData = value }

    private fun getApiData() = apiViewModel.liveData.observe(viewLifecycleOwner) { value -> binding.apiData = value }

    private fun getLocation() = apiViewModel.userLocation.observe(viewLifecycleOwner) { getApiData() }

    override fun onRefresh() = fetchNewData()

    private fun fetchNewData() {
        getDetectorData()
        getLocation()
        binding.isRefreshing = false
    }
}