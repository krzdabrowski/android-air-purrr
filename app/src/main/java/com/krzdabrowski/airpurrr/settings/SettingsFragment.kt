package com.krzdabrowski.airpurrr.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.lifecycle.observe
import androidx.preference.*
import com.google.android.material.snackbar.Snackbar
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.helper.PurifierHelper
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel.ForecastPredictionType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat(), PurifierHelper.SnackbarListener {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val purifierHelper: PurifierHelper by inject()

    private val keyAutoModeSwitch by lazy { getString(R.string.settings_key_automode_switch) }
    private val keyAutoModeThreshold by lazy { getString(R.string.settings_key_automode_threshold) }
    private val keyPerformanceHighLowSwitch by lazy { getString(R.string.settings_key_performance_highlow_switch) }
    private val keyForecastTypeRadioList by lazy { getString(R.string.settings_key_forecast_type_radio_list) }

    private val autoModeSwitchPreference by lazy { findPreference<SwitchPreference>(keyAutoModeSwitch) }
    private val autoModeThresholdPreference by lazy { findPreference<EditTextPreference>(keyAutoModeThreshold) }
    private val forecastTypeRadioListPreference by lazy { findPreference<ListPreference>(keyForecastTypeRadioList) }

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            keyAutoModeSwitch ->
                detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(keyAutoModeSwitch, false))
            keyAutoModeThreshold -> {
                detectorViewModel.autoModeThreshold.set(
                    try {
                        sharedPreferences.getString(keyAutoModeThreshold, "").toInt()
                    } catch (_: NumberFormatException) {
                        0
                    })
            }
            keyPerformanceHighLowSwitch -> {
                detectorViewModel.purifierHighLowObservableState.set(sharedPreferences.getBoolean(keyPerformanceHighLowSwitch, false))
                reactToPerformanceChanges(sharedPreferences, key)
            }
            keyForecastTypeRadioList -> {
                when (forecastTypeRadioListPreference!!.value) {
                    getString(R.string.settings_forecast_prediction_item_linear_regression) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.LINEAR_REGRESSION)
                    getString(R.string.settings_forecast_prediction_item_machine_learning) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.MACHINE_LEARNING)
                    getString(R.string.settings_forecast_prediction_item_neural_network) -> detectorViewModel.forecastPredictionType.set(ForecastPredictionType.NEURAL_NETWORK)
                }
            }
            else ->
                Timber.d("Unexpected key received: $key")
        }

        reactToAutoModeChanges(sharedPreferences, key)
    }

    // region Init
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        autoModeThresholdPreference?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            if (!text.isNullOrEmpty()) {
                "$text%"
            } else {
                getString(R.string.settings_dialog_no_data)
            }
        }
        autoModeThresholdPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setSelection(editText.text.length)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setToolbar()

        purifierHelper.snackbarListener = this
        getPurifierState()
        detectorViewModel.purifierOnOffObservableState.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                detectorViewModel.purifierOnOffState = purifierHelper.getPurifierOnOffState(detectorViewModel.purifierOnOffState)

                if (detectorViewModel.purifierHighLowObservableState.get()) {
                    detectorViewModel.checkPerformanceMode(true)
                }
            }
        })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }
    // endregion

    // region Purifier
    private fun getPurifierState() = detectorViewModel.currentWorkstateLiveData.observe(viewLifecycleOwner) { workstate -> purifierHelper.workstate = workstate }

    private fun reactToAutoModeChanges(sharedPreferences: SharedPreferences, key: String) {
        val isThresholdSet = !sharedPreferences.getString(keyAutoModeThreshold, "").isNullOrEmpty()
        val isThresholdClicked = key == keyAutoModeThreshold
        val isSwitchOn = sharedPreferences.getBoolean(keyAutoModeSwitch, false)

        if (isThresholdSet && !(isThresholdClicked && !isSwitchOn)) {
            detectorViewModel.checkAutoMode()
        } else if (!isThresholdSet && isThresholdClicked && isSwitchOn) {
            autoModeSwitchPreference?.isChecked = false
            detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(keyAutoModeSwitch, false))
        }
    }

    private fun reactToPerformanceChanges(sharedPreferences: SharedPreferences, key: String) {
        val isSwitchOn = sharedPreferences.getBoolean(key, false)
        detectorViewModel.checkPerformanceMode(isSwitchOn)
    }
    // endregion

    override fun showSnackbar(stringId: Int, length: Int) {
        Snackbar.make(view!!, stringId, length).show()
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.label_settings)
    }
}