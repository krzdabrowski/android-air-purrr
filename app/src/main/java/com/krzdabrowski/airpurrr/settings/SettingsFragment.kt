package com.krzdabrowski.airpurrr.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val switchPreference by lazy { findPreference<SwitchPreferenceCompat>(keySwitch) }
    private val thresholdPreference by lazy { findPreference<EditTextPreference>(keyThreshold) }
    private val keySwitch by lazy { getString(R.string.settings_key_switch) }
    private val keyThreshold by lazy { getString(R.string.settings_key_threshold) }

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            keySwitch -> detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(keySwitch, false))
            keyThreshold -> detectorViewModel.autoModeThreshold.set(
                    try {
                        sharedPreferences.getString(keyThreshold, "").toInt()
                    } catch (_: NumberFormatException) {
                        0
                    })
            else ->  // SeekBarPreference
                Snackbar.make(view!!, "Will be implemented later in phase 2", Snackbar.LENGTH_SHORT).show()
        }

        val isThresholdSet = !sharedPreferences.getString(keyThreshold, "").isNullOrEmpty()
        val isThresholdClicked = key == keyThreshold
        val isSwitchOn = sharedPreferences.getBoolean(keySwitch, false)

        if (isThresholdSet && !(isThresholdClicked && !isSwitchOn)) {
            detectorViewModel.checkAutoMode()
        } else if (!isThresholdSet && isThresholdClicked && isSwitchOn) {
            switchPreference?.isChecked = false
            detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(keySwitch, false))
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        thresholdPreference?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            if (!text.isNullOrEmpty()) {
                "$text%"
            } else {
                getString(R.string.settings_dialog_no_data)
            }
        }
        thresholdPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setSelection(editText.text.length)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }
}