package com.krzdabrowski.airpurrr.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.favre.lib.armadillo.Armadillo
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
    private val credentialPrefs by lazy { Armadillo.create(context, PREFS_LOGIN_KEY_CREDENTIALS).encryptionFingerprint(context).build() }
    private val email by lazy { credentialPrefs.getString(getString(R.string.login_pref_email), null) }
    private val password by lazy { credentialPrefs.getString(getString(R.string.login_pref_password), null) }

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
                controlPurifier(email, password, detectorViewModel.purifierState)
            }
        })

        checkLocationPermission()
    }

    // region Location permissions
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_LOCATION)
        } else {
            getLastKnownLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getLastKnownLocation()
            }
        }
    }

    private fun getLastKnownLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) apiViewModel.userLocation.value = location
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
                controlPurifier(email, password, detectorViewModel.purifierState)
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