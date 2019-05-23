package com.krzdabrowski.airpurrr.view

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.utils.PREFS_SETTINGS_KEY_SWITCH
import com.krzdabrowski.airpurrr.utils.PREFS_SETTINGS_KEY_THRESHOLD
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val switchPreference by lazy { findPreference<SwitchPreferenceCompat>(PREFS_SETTINGS_KEY_SWITCH) }
    private val thresholdPreference by lazy { findPreference<EditTextPreference>(PREFS_SETTINGS_KEY_THRESHOLD) }

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            PREFS_SETTINGS_KEY_SWITCH -> detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(PREFS_SETTINGS_KEY_SWITCH, false))
            PREFS_SETTINGS_KEY_THRESHOLD -> detectorViewModel.autoModeThreshold.set(
                try {
                    sharedPreferences.getString(PREFS_SETTINGS_KEY_THRESHOLD, "").toInt()
                } catch (_: NumberFormatException) {
                    0
                })
            else ->  // SeekBarPreference
                Snackbar.make(view!!, "Will be implemented later in phase 2", Snackbar.LENGTH_SHORT).show()
        }

        // to use auto-mode: threshold MUST NOT be empty (must be set)
        if (!sharedPreferences.getString(PREFS_SETTINGS_KEY_THRESHOLD, "").isNullOrEmpty() &&
                // it should not use edge-case when: threshold has been clicked and set && switch is off (case #5)
                !(key == PREFS_SETTINGS_KEY_THRESHOLD && !sharedPreferences.getBoolean(PREFS_SETTINGS_KEY_SWITCH, false))) {
            detectorViewModel.checkAutoMode()

            // it should block edge-case when: threshold has been clicked and NOT set && switch is on (case #8)
        } else if (sharedPreferences.getString(PREFS_SETTINGS_KEY_THRESHOLD, "").isNullOrEmpty() &&
                key == PREFS_SETTINGS_KEY_THRESHOLD && sharedPreferences.getBoolean(PREFS_SETTINGS_KEY_SWITCH, false)) {
            switchPreference?.isChecked = false
            detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(PREFS_SETTINGS_KEY_SWITCH, false))
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