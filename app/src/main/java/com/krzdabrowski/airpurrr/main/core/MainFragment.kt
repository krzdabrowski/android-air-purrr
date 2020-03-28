package com.krzdabrowski.airpurrr.main.core

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.BaseViewModel
import com.krzdabrowski.airpurrr.main.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import com.krzdabrowski.airpurrr.main.detector.ForecastPredictionType
import com.krzdabrowski.airpurrr.main.helper.PurifierHelper
import com.krzdabrowski.airpurrr.settings.SettingsFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(), PurifierHelper.SnackbarListener {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private val baseViewModel: BaseViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()
    private val permissionResultCodeLocation = 100
    private val sharedPrefs by lazy { PreferenceManager.getDefaultSharedPreferences(activity) }
    lateinit var forecastRefreshListener: ViewPagerRefreshListener

    // region Init
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setToolbar()
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        purifierHelper.snackbarListener = this

        view_pager.adapter = ViewPagerAdapter(this)
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
            {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (::forecastRefreshListener.isInitialized) {
                        when (position) {
                            FORECAST_SCREEN_POSITION -> forecastRefreshListener.onForecastRefresh()
                        }
                    }
                }
            }
        )

        TabLayoutMediator(tab_layout, view_pager) { currentTab, currentPosition ->
            currentTab.text = when (currentPosition) {
                CURRENT_SCREEN_POSITION -> context?.getString(R.string.main_tab_current)
                else -> context?.getString(R.string.main_tab_forecast)
            }
        }.attach()

        checkLocationPermission()
        configureForecastPredictionType()
        detectorViewModel.connectMqttClient()
        getPurifierState()
    }
    // endregion

    // region Location permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            permissionResultCodeLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getLastKnownLocation()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionResultCodeLocation)
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) apiViewModel.userLocation.value = location
        }
    }
    // endregion

    // region Purifier
    private fun configureForecastPredictionType() {
        val forecastPredictionType = sharedPrefs?.getString(getString(R.string.settings_key_forecast_type_radio_list), getString(R.string.settings_forecast_prediction_item_linear))
        when (forecastPredictionType) {
            getString(R.string.settings_forecast_prediction_item_linear) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.LINEAR)
            getString(R.string.settings_forecast_prediction_item_nonlinear) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.NONLINEAR)
            getString(R.string.settings_forecast_prediction_item_xgboost) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.XGBOOST)
            getString(R.string.settings_forecast_prediction_item_neural_network) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.NEURAL_NETWORK)
        }
    }

    private fun getPurifierState() = detectorViewModel.currentWorkstateLiveData.observe(viewLifecycleOwner) { workstate -> purifierHelper.workstate = workstate }

    private fun controlPurifierOnOff(currentState: Boolean) {
        detectorViewModel.purifierOnOffState = purifierHelper.getPurifierOnOffState(currentState)
        if (detectorViewModel.purifierHighLowObservableState.get()) {
            detectorViewModel.checkPerformanceMode(true)
        }
    }
    // endregion

    override fun showSnackbar(stringId: Int, length: Int) {
        Snackbar.make(requireView(), stringId, length).show()
    }

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_manual_mode -> {
                controlPurifierOnOff(detectorViewModel.purifierOnOffState)
                true
            }
            R.id.menu_settings -> {
                parentFragmentManager.commit {
                    replace(R.id.activity_main, SettingsFragment())
                    addToBackStack(null)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setToolbar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.app_name)
    }
    // endregion

    interface ViewPagerRefreshListener {
        fun onForecastRefresh()
    }
}