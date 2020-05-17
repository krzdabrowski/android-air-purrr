package com.krzdabrowski.airpurrr.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val keyAutoModeSwitch by lazy { getString(R.string.settings_key_automode_switch) }
    private val keyAutoModeThreshold by lazy { getString(R.string.settings_key_automode_threshold) }
    private val keyPerformanceHighLowSwitch by lazy { getString(R.string.settings_key_performance_highlow_switch) }
    private val keyForecastTypeRadioList by lazy { getString(R.string.settings_key_forecast_type_radio_list) }
    private val autoModeThresholdPreference by lazy { findPreference<EditTextPreference>(keyAutoModeThreshold) }
    private val forecastTypeRadioListPreference by lazy { findPreference<ListPreference>(keyForecastTypeRadioList) }

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            keyAutoModeSwitch -> {
                detectorViewModel.sendSettingsAutomodeState(sharedPreferences.getBoolean(key, false))
            }
            keyAutoModeThreshold -> {
                detectorViewModel.sendSettingsAutomodeThreshold(sharedPreferences.getString(key, "").toInt())
            }
            keyPerformanceHighLowSwitch -> {
                detectorViewModel.sendSettingsPerformancemodeState(sharedPreferences.getBoolean(key, false))
            }
            keyForecastTypeRadioList -> {
                when (forecastTypeRadioListPreference!!.value) {
                    getString(R.string.settings_forecast_prediction_item_linear) -> detectorViewModel.subscribeToForecastLinearValues()
                    getString(R.string.settings_forecast_prediction_item_nonlinear) -> detectorViewModel.subscribeToForecastNonlinearValues()
                    getString(R.string.settings_forecast_prediction_item_xgboost) -> detectorViewModel.subscribeToForecastXGBoostValues()
                    getString(R.string.settings_forecast_prediction_item_neural_network) -> detectorViewModel.subscribeToForecastNeuralNetworkValues()
                }
            }
            else -> Timber.d("Unexpected key received: $key")
        }
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

    private fun setToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.label_settings)
    }
}