package com.example.trubul.airpurrr

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.loader.app.LoaderManager
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.loader.content.Loader

import com.example.trubul.airpurrr.databinding.ActivityMainBinding

import java.util.Timer
import java.util.TimerTask

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.partial_main_data.*
import timber.log.Timber

// TODO: Navigation Component
// TODO: animations
// TODO: deal with every single deprecated library to use AndroidX version (or alternative other library -> for ex. ProgressDialog)
// TODO: sprawdzic wszystkie id czy sa potrzebne i czy przestrzegaja zasad dobrego id
// TODO: implement good practices (https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md)
// TODO: (for future) implement TabLayout with current and predicted results/data in fragments & remove automatic switch (only manual left)
// TODO: export these strings somewhere
// TODO: Snackbars instead of Toasts
// TODO: remove as much butterknife as possible & implement more .XML databinding
// TODO: create model (Data: DetectorData, ApiData or so), viewmodel, view, helpers, ... packages
// TODO: consider to do something with getters/setters -> Observable variables?
// TODO: remove loaders while implementing MVVM with LiveData

private const val LOADER_DETECTOR = 1
private const val LOADER_API_PM = 2

class MainActivity : AppCompatActivity(), // SwipeListener.SwipeCallback,
        SwitchHelper.SwitchCallback, LoaderManager.LoaderCallbacks<Any>, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var pmValuesDetector: List<Double>
    private lateinit var pmValuesAndDatesAPI: List<Any>
    private lateinit var pmValuesAPI: List<Double>
    private lateinit var pmDatesAPI: List<String>

    private lateinit var manualListener: SwitchHelper
    private lateinit var activityMainBinding: ActivityMainBinding

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
        if (pmValuesAndDatesAPI != null) {
            pmValuesAPI = pmValuesAndDatesAPI[0] as List<Double>
            pmDatesAPI = pmValuesAndDatesAPI[1] as List<String>
        }
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
        Timber.plant(Timber.DebugTree())

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        pmValuesDetector = listOf(0.0, 0.0)
        pmValuesAPI = listOf(0.0, 0.0)
        pmDatesAPI = listOf(getString(R.string.main_data_info_api_empty), getString(R.string.main_data_info_api_empty))

        LoaderManager.getInstance(this).initLoader<Any>(LOADER_DETECTOR, null, this).forceLoad()  // Loader for Detector PM data
        LoaderManager.getInstance(this).initLoader<Any>(LOADER_API_PM, null, this).forceLoad()  // Loader for API PM data
        automaticDownload()  // downloadPMValues DetectorHelper values every 1 minute

        manualListener = SwitchHelper(this, this)
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
        when (item.itemId) {
            R.id.menu_threshold ->
                //                alertDialog.createDialog();
                return true
            else -> return super.onOptionsItemSelected(item)
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
        var layout: ConstraintLayout

        // Set TextView colors
        for (i in 0..1) {
            // First iteration = update PM2.5, second iteration = update PM10
            if (i == 0) {
                layout = activityMainBinding.partialMainDataPm25.layoutMainData
                activityMainBinding.partialMainDataPm25.dataType.text = getString(R.string.main_data_info_pm25)
            } else {
                layout = activityMainBinding.partialMainDataPm10.layoutMainData
                activityMainBinding.partialMainDataPm10.dataType.text = getString(R.string.main_data_info_pm10)
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
        activityMainBinding.partialMainDataPm25.dataPercentage.text = getString(R.string.main_data_percentage, pmValues!![0])
        activityMainBinding.partialMainDataPm10.dataPercentage.text = getString(R.string.main_data_percentage, pmValues[1])
        activityMainBinding.partialMainDataPm25.dataUgm3.text = getString(R.string.main_data_ugm3, pmValues[0] / 4)
        activityMainBinding.partialMainDataPm10.dataUgm3.text = getString(R.string.main_data_ugm3, pmValues[1] / 2)

        // Set TextView mode
        if (!flagDetectorAPI) {  // if detector
            activityMainBinding.partialMainDataPm25.dataSource.setText(R.string.main_data_info_indoors)
            activityMainBinding.partialMainDataPm10.dataSource.setText(R.string.main_data_info_indoors)
        } else {  // if APIHelper
            activityMainBinding.partialMainDataPm25.dataSource.text = getString(R.string.main_data_info_api, pmDatesAPI!![0])
            activityMainBinding.partialMainDataPm10.dataSource.text = getString(R.string.main_data_info_api, pmDatesAPI!![1])
        }
    }

    companion object {
        internal const val DETECTOR_URL = "http://airpurrr.ga/pm_data.txt"
        internal var flagDetectorAPI = false  // false = DetectorMode, true = APIMode

        private var mSharedPreferences: SharedPreferences? = null

        // Get login_email and login_password from LoginActivity
        internal val hashedEmail: String?
            get() = mSharedPreferences!!.getString(LoginActivity.SAVED_HASH_EMAIL_KEY, null)

        internal val hashedPassword: String?
            get() = mSharedPreferences!!.getString(LoginActivity.SAVED_HASH_PASSWORD_KEY, null)
    }
}
