package com.example.trubul.airpurrr.activity

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.helper.SwitchHelper
import com.example.trubul.airpurrr.helper.ConversionHelper
import com.example.trubul.airpurrr.model.Api
import com.example.trubul.airpurrr.model.Detector
import com.example.trubul.airpurrr.retrofit.ApiService
import com.example.trubul.airpurrr.retrofit.DetectorService

import java.util.Timer
import java.util.TimerTask

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.partial_main_data.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

// TODO: implement coroutines & POST login with retrofit
// TODO: implement more .XML databinding
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

    private lateinit var manualListener: SwitchHelper

    private var flagDetectorAPI = false  // false = DetectorMode, true = APIMode
    private var pmValuesDetector = mutableListOf(0.0, 0.0)
    private var pmValuesAPI = mutableListOf(0.0, 0.0)
    private var pmDatesAPI = ""


    override fun setSwitchManual(state: Boolean) {
        switch_manual.isChecked = state
    }

    private fun setSwipeRefreshing(value: Boolean) {
        swipe_refresh.post { swipe_refresh.isRefreshing = value }
    }

    // Update UI
    private fun updateDetector() {
        flagDetectorAPI = false
        setUI(pmValuesDetector)
    }

    private fun updateAPI() {
        flagDetectorAPI = true
        setUI(pmValuesAPI)
    }

    private fun automaticDownload() {
        val timer = Timer()
        val minuteTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    retrofitDetector()
                    retrofitApi()
                }
            }
        }
        timer.schedule(minuteTask, 0, (1000 * 60).toLong())  // 1000*60*1 every 1 minute
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val hashedEmail = sharedPreferences.getString(getString(R.string.login_pref_email), null)
        val hashedPassword = sharedPreferences.getString(getString(R.string.login_pref_password), null)

        manualListener = SwitchHelper(swipe_refresh, hashedEmail, hashedPassword, this)
        automaticDownload()  // downloadPMValues DetectorHelper values every 1 minute
        swipe_refresh.setOnRefreshListener(this)
        switch_manual.setOnCheckedChangeListener(manualListener)

        val textViewListener = {
            if (flagDetectorAPI) {
                updateDetector()
            } else {
                updateAPI()
            }
        }

        partial_main_data_pm25.setOnClickListener { textViewListener() }
        partial_main_data_pm10.setOnClickListener { textViewListener() }
    }

    fun retrofitDetector() {
        val service by lazy { DetectorService.create() }

        val call = service.getDetectorData()
        call.enqueue(object : Callback<Detector.Result> {
            override fun onResponse(call: Call<Detector.Result>, response: Response<Detector.Result>) {
                val data = response.body()
                if (data != null && data.values != null) {
                    pmValuesDetector[0] = ConversionHelper.pm25ToPercent(data.values.pm25)
                    pmValuesDetector[1] = ConversionHelper.pm10ToPercent(data.values.pm10)
                    updateDetector()
                }
            }

            override fun onFailure(call: Call<Detector.Result>, t: Throwable) {
                pmValuesDetector[0] = 0.0
                pmValuesDetector[1] = 0.0
                updateDetector()
            }
        })
    }

    fun retrofitApi() {
        val service by lazy { ApiService.create() }

        val callPm25 = service.getApiPm25Data()
        val callPm10 = service.getApiPm10Data()

        callPm25.enqueue(object : Callback<Api.Result> {
            override fun onResponse(call: Call<Api.Result>, response: Response<Api.Result>) {
                val data = response.body()
                if (data != null) {
                    for (i in data.values.indices) {
                        if (data.values[i].value != null) {
                            pmValuesAPI[0] = ConversionHelper.pm25ToPercent(data.values[i].value.toDouble())
                            pmDatesAPI = data.values[i].date
                            break
                        } else continue
                    }
                }
            }

            override fun onFailure(call: Call<Api.Result>, t: Throwable) {
                pmValuesAPI[0] = 0.0
                pmDatesAPI = getString(R.string.main_data_info_api_empty)
            }
        })

        callPm10.enqueue(object : Callback<Api.Result> {
            override fun onResponse(call: Call<Api.Result>, response: Response<Api.Result>) {
                val data = response.body()
                if (data != null) {
                    for (i in data.values.indices) {
                        if (data.values[i].value != null) {
                            pmValuesAPI[1] = ConversionHelper.pm10ToPercent(data.values[i].value.toDouble())
                            pmDatesAPI = data.values[i].date
                            break
                        } else continue
                    }
                }
            }

            override fun onFailure(call: Call<Api.Result>, t: Throwable) {
                pmValuesAPI[1] = 0.0
                pmDatesAPI = getString(R.string.main_data_info_api_empty)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_threshold -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        retrofitDetector()
        retrofitApi()
        setSwipeRefreshing(false)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)  // disable going back to the LoginActivity
    }

    private fun setUI(pmValues: List<Double>?) {
        var layout: View

        // Set TextView colors
        for (i in 0..1) {
            // First iteration = update PM2.5, second iteration = update PM10
            if (i == 0) {
                layout = partial_main_data_pm25
                layout.data_type.text = getString(R.string.main_data_info_pm25)
            } else {
                layout = partial_main_data_pm10
                layout.data_type.text = getString(R.string.main_data_info_pm10)
            }

            // Update colors
            if (pmValues!![i] == 0.0) {  // connection error
                layout.setBackgroundResource(R.drawable.data_unavailable)
            } else if (pmValues[i] > 0 && pmValues[i] <= 50) {
                layout.setBackgroundResource(R.drawable.data_green)
            } else if (pmValues[i] > 50 && pmValues[i] <= 100) {
                layout.setBackgroundResource(R.drawable.data_lime)
            } else if (pmValues[i] > 100 && pmValues[i] <= 200) {
                layout.setBackgroundResource(R.drawable.data_yellow)
            } else {
                layout.setBackgroundResource(R.drawable.data_red)
            }
        }

        // Set TextView PM values
        partial_main_data_pm25.data_percentage.text = getString(R.string.main_data_percentage, pmValues!![0])
        partial_main_data_pm10.data_percentage.text = getString(R.string.main_data_percentage, pmValues[1])
        partial_main_data_pm25.data_ugm3.text = getString(R.string.main_data_ugm3, pmValues[0] / 4)
        partial_main_data_pm10.data_ugm3.text = getString(R.string.main_data_ugm3, pmValues[1] / 2)

        // Set TextView mode
        if (!flagDetectorAPI) {  // if detector
            partial_main_data_pm25.data_source.setText(R.string.main_data_info_indoors)
            partial_main_data_pm10.data_source.setText(R.string.main_data_info_indoors)
        } else {  // if APIHelper
            partial_main_data_pm25.data_source.text = getString(R.string.main_data_info_api, pmDatesAPI)
            partial_main_data_pm10.data_source.text = getString(R.string.main_data_info_api, pmDatesAPI)
        }
    }
}
