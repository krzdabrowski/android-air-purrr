package com.krzdabrowski.airpurrr.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.helper.*
import com.krzdabrowski.airpurrr.viewmodel.ApiViewModel
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_data_current.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {
    private val detectorViewModel: DetectorViewModel by viewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private val hashedEmail by lazy { sharedPreferences.getString(getString(R.string.login_pref_email), "") }
    private val hashedPassword by lazy { sharedPreferences.getString(getString(R.string.login_pref_password), "") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_pager.adapter = PagerAdapter(context!!, childFragmentManager)
        tab_layout.setupWithViewPager(view_pager)

        detectorViewModel.purifierObservableState.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                controlPurifier(hashedEmail!!, hashedPassword!!, detectorViewModel.purifierState)
            }
        })

        checkLocationPermission()
    }

    // region Location permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastKnownLocation()
                } else {
                    apiViewModel.userLocation = apiViewModel.getDefaultLocation()
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_LOCATION)
        } else {
            getLastKnownLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                apiViewModel.userLocation = location
            } else {
                apiViewModel.userLocation = apiViewModel.getDefaultLocation()
            }
        }
    }
    // endregion

    // region Purifier
    private fun controlPurifier(email: String, password: String, state: Boolean) {
        detectorViewModel.getLiveData().observe(this, Observer { workstateValue ->
            detectorViewModel.purifierState = purifierHelper.getPurifierState(workstateValue, email, password, state, swipe_refresh)
        })
    }
    // endregion

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_manual_mode -> {
                controlPurifier(hashedEmail!!, hashedPassword!!, detectorViewModel.purifierState)
                true
            }
            R.id.mnu_settings -> {
                findNavController().navigate(R.id.navigate_to_settings_screen)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion
}