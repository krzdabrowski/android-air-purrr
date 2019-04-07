package com.example.trubul.airpurrr.view

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.databinding.ActivityMainBinding
import com.example.trubul.airpurrr.di.networkModule
import com.example.trubul.airpurrr.di.repositoryModule
import com.example.trubul.airpurrr.di.viewModelModule
import com.example.trubul.airpurrr.helper.SwitchHelper
import com.example.trubul.airpurrr.viewmodel.ApiViewModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import java.util.Timer
import java.util.TimerTask
import kotlinx.android.synthetic.main.activity_main.*
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

class MainActivity : AppCompatActivity(),
        SwitchHelper.SwitchCallback, SwipeRefreshLayout.OnRefreshListener {

    private val detectorViewModel: DetectorViewModel by viewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
//    private lateinit var manualListener: SwitchHelper

    override fun setSwitchManual(state: Boolean) {
        switch_manual.isChecked = state
    }

    private fun setSwipeRefreshing(value: Boolean) {
        swipe_refresh.post { swipe_refresh.isRefreshing = value }
    }

    private fun automaticDownload() {
        val timer = Timer()
        val minuteTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
//                    downloadApi()
                    downloadDetector()
                }
            }
        }
        timer.schedule(minuteTask, 0, (1000 * 60).toLong())  // 1000*60*1 every 1 minute
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }

        binding.lifecycleOwner = this
        binding.detectorVm = detectorViewModel
        binding.apiVm = apiViewModel
        binding.flagDetectorApi = false

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val hashedEmail = sharedPreferences.getString(getString(R.string.login_pref_email), null)
        val hashedPassword = sharedPreferences.getString(getString(R.string.login_pref_password), null)

//        manualListener = SwitchHelper(swipe_refresh, hashedEmail, hashedPassword, this)
        automaticDownload()  // downloadPMValues DetectorHelper values every 1 minute
        swipe_refresh.setOnRefreshListener(this)
//        switch_manual.setOnCheckedChangeListener(manualListener)
    }

    private fun downloadDetector() {
        detectorViewModel.getData().observe(this, Observer { value -> binding.detectorResult = value })
    }

//    private fun downloadApi() {
//        val liveData = apiViewModel.getData()
//        liveData.observe(this, Observer { Toast.makeText(this@MainActivity, "API download finished", Toast.LENGTH_SHORT).show() })
//    }

    override fun onRefresh() {
//        downloadApi()
        downloadDetector()
        setSwipeRefreshing(false)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)  // disable going back to the LoginActivity
    }
}
