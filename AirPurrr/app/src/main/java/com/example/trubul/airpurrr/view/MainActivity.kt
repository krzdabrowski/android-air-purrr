package com.example.trubul.airpurrr.view

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.databinding.ActivityMainBinding
import com.example.trubul.airpurrr.helper.PurifierHelper
import com.example.trubul.airpurrr.viewmodel.ApiViewModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import java.util.Timer
import java.util.TimerTask
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val detectorViewModel: DetectorViewModel by viewModel()
    private val apiViewModel: ApiViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()
    private lateinit var binding: ActivityMainBinding

    private var hashedEmail: String? = ""
    private var hashedPassword: String? = ""
    private var manualModeState = false

    private fun getDetectorData() = detectorViewModel.getLiveData().observe(this, Observer { value -> binding.detectorData = value })
    private fun getApiData() = apiViewModel.getLiveData().observe(this, Observer { value -> binding.apiData = value })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.flagDetectorApi = false

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        hashedEmail = sharedPreferences.getString(getString(R.string.login_pref_email), "")
        hashedPassword = sharedPreferences.getString(getString(R.string.login_pref_password), "")

        partial_main_data_pm25.setOnClickListener { onDataClick() }
        partial_main_data_pm10.setOnClickListener { onDataClick() }
        swipe_refresh.setOnRefreshListener { onRefresh() }

        automaticDownload()
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

    private fun onManualModeClick(email: String, password: String, state: Boolean) {
        detectorViewModel.getLiveData().observe(this, Observer { workstateValue ->
            purifierHelper.handlePurifierStates(workstateValue, email, password, state, swipe_refresh) }
        )
    }

    private fun onRefresh() {
        getApiData()
        getDetectorData()
        swipe_refresh.isRefreshing = false
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.mnu_manual_mode -> {
                onManualModeClick(hashedEmail!!, hashedPassword!!, manualModeState)
                manualModeState = purifierHelper.state
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
