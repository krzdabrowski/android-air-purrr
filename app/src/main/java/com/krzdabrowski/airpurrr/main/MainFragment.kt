package com.krzdabrowski.airpurrr.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import at.favre.lib.armadillo.Armadillo
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.krzdabrowski.airpurrr.settings.SettingsFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(), PurifierHelper.SnackbarListener {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()

    private val credentialPrefs by lazy { Armadillo.create(context, getString(R.string.login_key_credentials)).encryptionFingerprint(context).build() }
    private val email by lazy { credentialPrefs.getString(getString(R.string.login_pref_email), null) }
    private val password by lazy { credentialPrefs.getString(getString(R.string.login_pref_password), null) }
    private val permissionResultCodeLocation = 100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.app_name)

        purifierHelper.listener = this

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_pager.adapter = ViewPagerAdapter(context!!, childFragmentManager)
        tab_layout.setupWithViewPager(view_pager)

        checkLocationPermission()
    }

    // region Location permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            permissionResultCodeLocation -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getLastKnownLocation()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionResultCodeLocation)
        } else {
            getLastKnownLocation()
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
    private fun controlPurifierOnOff(email: String, password: String, currentState: Boolean) {
        detectorViewModel.getLiveData().observe(viewLifecycleOwner, Observer { workstateValue ->
            detectorViewModel.purifierOnOffState = purifierHelper.getPurifierOnOffState(workstateValue, email, password, currentState)
            if (detectorViewModel.purifierHighLowObservableState.get()) {
                detectorViewModel.checkPerformanceMode(true, email, password)
            }
        })
    }
    // endregion

    override fun showSnackbar(stringId: Int, length: Int) {
        Snackbar.make(view!!, stringId, length).show()
    }

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_manual_mode -> {
                controlPurifierOnOff(email, password, detectorViewModel.purifierOnOffState)
                true
            }
            R.id.menu_settings -> {
                fragmentManager?.commit {
                    replace(R.id.main_activity, SettingsFragment())
                    addToBackStack(null)
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion
}