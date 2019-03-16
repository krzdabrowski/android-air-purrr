package com.example.trubul.airpurrr

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.loader.app.LoaderManager
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.loader.content.Loader
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch

import com.example.trubul.airpurrr.databinding.ActivityMainBinding

import java.util.Arrays
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife

// TODO: Navigation Component
// TODO: animations
// TODO: Timber library instead of log.x
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

private const val TAG = "MainActivity"
internal const val DETECTOR_URL = "http://airpurrr.ga/pm_data.txt"

private const val LOADER_DETECTOR = 1
private const val LOADER_API_PM = 2

class MainActivity : AppCompatActivity(), // SwipeListener.SwipeCallback,
        SwitchHelper.SwitchCallback, DetectorHelper.DetectorCallback, APIHelper.APICallback, LoaderManager.LoaderCallbacks<*>, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.switch_manual)
    internal var switchManual: Switch? = null
    @BindView(R.id.swipe_refresh)
    internal lateinit var mySwipeRefreshLayout: SwipeRefreshLayout

    private val detector = DetectorHelper(this)
    private val api = APIHelper(this)  // must-be instance to make mCallback work

    private lateinit var pmValuesDetector: Array<Double>
    private lateinit var pmValuesAndDatesAPI: List<Any>
    private lateinit var pmValuesAPI: Array<Double>
    private lateinit var pmDatesAPI: Array<String>

    private lateinit var manualListener: SwitchHelper
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun setSwitchManual(state: Boolean) {
        switchManual!!.isChecked = state
    }

    override fun setPMValuesAndDatesAPI(pmValuesAndDatesAPI: List<Any>) {
        this.pmValuesAndDatesAPI = pmValuesAndDatesAPI
    }

    override fun setPMValuesDetector(pmValuesDetector: Array<Double>) {
        this.pmValuesDetector = pmValuesDetector
    }

    private fun setSwipeRefreshing(value: Boolean) {
        mySwipeRefreshLayout.post { mySwipeRefreshLayout.isRefreshing = value }
    }

    // Update UI
    private fun updateDetector() {
        flagDetectorAPI = false
        setUI(pmValuesDetector)
    }

    private fun updateAPI() {
        flagDetectorAPI = true
        if (pmValuesAndDatesAPI != null) {
            pmValuesAPI = pmValuesAndDatesAPI[0] as Array<Double>
            pmDatesAPI = pmValuesAndDatesAPI[1] as Array<String>
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
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ButterKnife.bind(this)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        pmValuesDetector = arrayOf(0.0, 0.0)
        pmValuesAPI = arrayOf(0.0, 0.0)
        pmDatesAPI = arrayOf(getString(R.string.main_data_info_api_empty), getString(R.string.main_data_info_api_empty))

        LoaderManager.getInstance(this).initLoader<Any>(LOADER_DETECTOR, null, this).forceLoad()  // Loader for Detector PM data
        LoaderManager.getInstance(this).initLoader<Any>(LOADER_API_PM, null, this).forceLoad()  // Loader for API PM data
        automaticDownload()  // downloadPMValues DetectorHelper values every 1 minute

        manualListener = SwitchHelper(this, this)
        mySwipeRefreshLayout.setOnRefreshListener(this)

        activityMainBinding.switchManual.setOnCheckedChangeListener(manualListener)

        val textViewListener = {
            if (flagDetectorAPI) {
                updateDetector()
            } else {
                updateAPI()
            }
        }

        activityMainBinding.partialMainDataPm25.layoutMainData.setOnClickListener(textViewListener)
        activityMainBinding.partialMainDataPm10.layoutMainData.setOnClickListener(textViewListener)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<*> {
        if (id == LOADER_DETECTOR) {
            return DetectorHelper.Loader(this)
        } else if (id == LOADER_API_PM) {
            return APIHelper.PMLoader(this)
        }
        return null
    }

    override fun onLoadFinished(loader: Loader<*>, data: Any) {
        val id = loader.id

        if (id == LOADER_DETECTOR) {
            pmValuesDetector = data as Array<Double>
            updateDetector()
        } else if (id == LOADER_API_PM) {
            pmValuesAndDatesAPI = data as List<Any>
        }
    }

    override fun onLoaderReset(loader: Loader<*>) {}

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

    private fun setUI(pmValues: Array<Double>?) {
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
            if (pmValues!![i] == 0) {  // connection error
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

        internal var flagDetectorAPI = false  // false = DetectorMode, true = APIMode

        private var mSharedPreferences: SharedPreferences? = null

        // Get login_email and login_password from LoginActivity
        internal val hashedEmail: String?
            get() = mSharedPreferences!!.getString(LoginActivity.SAVED_HASH_EMAIL_KEY, null)

        internal val hashedPassword: String?
            get() = mSharedPreferences!!.getString(LoginActivity.SAVED_HASH_PASSWORD_KEY, null)
    }
}
