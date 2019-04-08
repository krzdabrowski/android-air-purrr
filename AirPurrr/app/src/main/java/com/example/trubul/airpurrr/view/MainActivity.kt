package com.example.trubul.airpurrr.view

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.databinding.ActivityMainBinding
import com.example.trubul.airpurrr.di.helperModule
import com.example.trubul.airpurrr.di.networkModule
import com.example.trubul.airpurrr.di.repositoryModule
import com.example.trubul.airpurrr.di.viewModelModule
import com.example.trubul.airpurrr.helper.SwitchHelper
import com.example.trubul.airpurrr.viewmodel.ApiViewModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import java.util.Timer
import java.util.TimerTask
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import timber.log.Timber

// TODO: implement databinding
// TODO: implement MVVM with LiveData
// TODO: airly API instead of public

// TODO: (later) DEPRECATIONS - deal with every single deprecated library to use AndroidX version (or alternative other library -> for ex. ProgressDialog)
// TODO: (later) UI REWORK - implement TabLayout with current and predicted results/data in fragments
// TODO: (later) UI REWORK - switch manual control on toolbar, alwaysOn & remove switch with logic
// TODO: (later) UI REWORK - Preferences instead of menu + settings menu with alwaysOn state (dialogs inflated from new file instead on making everything in Java code (lecture 255 on Udemy))
// TODO: (later) UI REWORK - selectors for menu items on toolbar

// TODO: (if time) Navigation Component
// TODO: (if time) animations
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
        binding.lifecycleOwner = this
        binding.flagDetectorApi = false

        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(listOf(networkModule, helperModule, repositoryModule, viewModelModule))
        }

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
