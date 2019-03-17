package com.example.trubul.airpurrr.activity

import androidx.loader.app.LoaderManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.loader.content.Loader
import com.example.trubul.airpurrr.APIHelper
import com.example.trubul.airpurrr.DetectorHelper
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.SwitchHelper

import java.util.Timer
import java.util.TimerTask

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.partial_main_data.view.*
import timber.log.Timber

// TODO: implement retrofit
// TODO: implement more .XML databinding
// TODO: create model (Data: DetectorData, ApiData or so), viewmodel, view, helpers, ... packages
// TODO: remove loaders while implementing MVVM with LiveData

// TODO: (later) DEPRECATIONS - deal with every single deprecated library to use AndroidX version (or alternative other library -> for ex. ProgressDialog)
// TODO: (later) UI REWORK - implement TabLayout with current and predicted results/data in fragments
// TODO: (later) UI REWORK - switch manual control on toolbar, alwaysOn & remove switch with logic
// TODO: (later) UI REWORK - Preferences instead of menu + settings menu with alwaysOn state (dialogs inflated from new file instead on making everything in Java code (lecture 255 on Udemy))
// TODO: (later) UI REWORK - selectors for menu items on toolbar

// TODO: (if time) Navigation Component
// TODO: (if time) animations
// TODO: (at the end) implement good practices (https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md)
// TODO: (at the end) check if all ids are needed and are correct with good practices

private const val LOADER_DETECTOR = 1
private const val LOADER_API_PM = 2

class MainActivity : AppCompatActivity(),
        SwitchHelper.SwitchCallback, LoaderManager.LoaderCallbacks<Any>, SwipeRefreshLayout.OnRefreshListener {

    private var flagDetectorAPI = false  // false = DetectorMode, true = APIMode
    private var pmValuesDetector = listOf(0.0, 0.0)
    private var pmValuesAPI = listOf(0.0, 0.0)
    private lateinit var pmDatesAPI: List<String>
    private lateinit var pmValuesAndDatesAPI: List<Any>
    private lateinit var manualListener: SwitchHelper

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
        pmValuesAPI = pmValuesAndDatesAPI[0] as List<Double>
        pmDatesAPI = pmValuesAndDatesAPI[1] as List<String>
        setUI(pmValuesAPI)
    }

    private fun automaticDownload() {
        val timer = Timer()
        val minuteTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { LoaderManager.getInstance(this@MainActivity).initLoader<Any>(LOADER_DETECTOR, null, this@MainActivity).forceLoad() }
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

        pmDatesAPI = listOf(getString(R.string.main_data_info_api_empty), getString(R.string.main_data_info_api_empty))
        manualListener = SwitchHelper(swipe_refresh, hashedEmail, hashedPassword, this)

        LoaderManager.getInstance(this).initLoader<Any>(LOADER_DETECTOR, null, this).forceLoad()  // Loader for Detector PM data
        LoaderManager.getInstance(this).initLoader<Any>(LOADER_API_PM, null, this).forceLoad()  // Loader for API PM data
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Any> {
        return if (id == LOADER_DETECTOR) {
            DetectorHelper.Loader(this) as Loader<Any>
        } else {
            APIHelper.PMLoader(this) as Loader<Any>
        }
    }

    override fun onLoadFinished(loader: Loader<Any>, data: Any) {
        val id = loader.id

        if (id == LOADER_DETECTOR) {
            pmValuesDetector = data as List<Double>
            updateDetector()
        } else if (id == LOADER_API_PM) {
            pmValuesAndDatesAPI = data as List<Any>
        }
    }

    override fun onLoaderReset(loader: Loader<Any>) {}

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
        LoaderManager.getInstance(this).initLoader<Any>(LOADER_DETECTOR, null, this).forceLoad()
        LoaderManager.getInstance(this).initLoader<Any>(LOADER_API_PM, null, this).forceLoad()
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
            partial_main_data_pm25.data_source.text = getString(R.string.main_data_info_api, pmDatesAPI[0])
            partial_main_data_pm10.data_source.text = getString(R.string.main_data_info_api, pmDatesAPI[1])
        }
    }
}
