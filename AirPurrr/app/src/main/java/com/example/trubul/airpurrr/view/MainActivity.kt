package com.example.trubul.airpurrr.view

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.databinding.ActivityMainBinding
import com.example.trubul.airpurrr.helper.SwitchHelper
import com.example.trubul.airpurrr.viewmodel.ApiViewModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import java.util.Timer
import java.util.TimerTask
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: DEPRECATIONS - deal with every single deprecated library to use AndroidX version (or alternative other library -> for ex. ProgressDialog, AuthCallback)

// TODO: (later) UI REWORK - implement TabLayout with current and predicted results/data in fragments
// TODO: (later) UI REWORK - switch manual control on toolbar, alwaysOn & remove switch with logic
// TODO: (later) UI REWORK - Preferences instead of menu + settings menu with alwaysOn state (dialogs inflated from new file instead on making everything in Java code (lecture 255 on Udemy))
// TODO: (later) UI REWORK - selectors for menu items on toolbar
// TODO: (later) UI REWORK - Navigation Component
// TODO: (later) UI REWORK - animations

// TODO: (RE-IMPLEMENT) Automatic Mode logic & threshold dialog
// TODO: (RE-IMPLEMENT) Location
// TODO: (RE-IMPLEMENT) Fingerprint Reader logic & network/fingerprint checks and permissions & sha512 impl from some library

// TODO: (at the end) implement good practices (https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md)
// TODO: (at the end) check if all ids are needed and are correct with good practices

class MainActivity : BaseActivity() {
    private val detectorViewModel: DetectorViewModel by viewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private val switchHelper: SwitchHelper by inject()
    private lateinit var binding: ActivityMainBinding

    private fun getDetectorData() = detectorViewModel.getLiveData().observe(this, Observer { value -> binding.detectorData = value })
    private fun getApiData() = apiViewModel.getLiveData().observe(this, Observer { value -> binding.apiData = value })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.flagDetectorApi = false

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val hashedEmail = sharedPreferences.getString(getString(R.string.login_pref_email), "")
        val hashedPassword = sharedPreferences.getString(getString(R.string.login_pref_password), "")

        partial_main_data_pm25.setOnClickListener { onDataClick() }
        partial_main_data_pm10.setOnClickListener { onDataClick() }
        binding.setOnSwitchChange { switchView, isChecked -> onSwitchClick(switchView, isChecked, hashedEmail!!, hashedPassword!!) }
        swipe_refresh.setOnRefreshListener { onRefresh() }

        automaticDownload()  // downloadPMValues DetectorHelper values every 1 minute
    }

    private fun automaticDownload() {
        val timer = Timer()
        val minuteTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    getApiData()
                    getDetectorData()
                }
            }
        }
        timer.schedule(minuteTask, 0, (1000 * 60).toLong())  // 1000*60*1 every 1 minute
    }

    private fun onDataClick() {
        binding.flagDetectorApi = !binding.flagDetectorApi!!
    }

    private fun onSwitchClick(switchView: CompoundButton, isChecked: Boolean, email: String, password: String) {
        if (switchHelper.oldSwitchState != isChecked) {
            detectorViewModel.getLiveData().observe(this, Observer { workstateValue ->
                switchHelper.handleFanStates(workstateValue, switchView, swipe_refresh, email, password, isChecked)
            })
        }
    }

    private fun onRefresh() {
        getApiData()
        getDetectorData()
        swipe_refresh.isRefreshing = false
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
