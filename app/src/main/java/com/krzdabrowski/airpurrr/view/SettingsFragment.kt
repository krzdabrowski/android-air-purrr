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

        val isThresholdSet = !sharedPreferences.getString(PREFS_SETTINGS_KEY_THRESHOLD, "").isNullOrEmpty()
        val isThresholdClicked = key == PREFS_SETTINGS_KEY_THRESHOLD
        val isSwitchOn = sharedPreferences.getBoolean(PREFS_SETTINGS_KEY_SWITCH, false)

        if (isThresholdSet && !(isThresholdClicked && !isSwitchOn)) {
            detectorViewModel.checkAutoMode()
        } else if (!isThresholdSet && isThresholdClicked && isSwitchOn) {
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