package com.krzdabrowski.airpurrr.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import at.favre.lib.armadillo.Armadillo
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.krzdabrowski.airpurrr.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), PurifierHelper.SnackbarListener {
    private val detectorViewModel: DetectorViewModel by viewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()

    private val credentialPrefs by lazy { Armadillo.create(this, getString(R.string.login_key_credentials)).encryptionFingerprint(this).build() }
    private val email by lazy { credentialPrefs.getString(getString(R.string.login_pref_email), null) }
    private val password by lazy { credentialPrefs.getString(getString(R.string.login_pref_password), null) }
    private val permissionResultCodeLocation = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        onBackPressedDispatcher.addCallback { moveTaskToBack(true) }

        view_pager.adapter = ViewPagerAdapter(this, supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
        purifierHelper.listener = this

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionResultCodeLocation)
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) apiViewModel.userLocation.value = location
        }
    }
    // endregion

    // region Purifier
    private fun controlPurifier(email: String, password: String, currentState: Boolean) {
        detectorViewModel.getLiveData().observe(this, Observer { workstateValue ->
            detectorViewModel.purifierState = purifierHelper.getPurifierState(workstateValue, email, password, currentState)
        })
    }

    override fun showSnackbar(stringId: Int, length: Int) {
        Snackbar.make(findViewById(android.R.id.content), stringId, length).show()
    }
    // endregion

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_manual_mode -> {
                controlPurifier(email, password, detectorViewModel.purifierState)
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion
}
