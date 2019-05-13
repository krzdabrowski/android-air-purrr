package com.krzdabrowski.airpurrr.view

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val thresholdPreference by lazy { findPreference<EditTextPreference>(keyThreshold) }
    private val keyThreshold = "autoModeThreshold"
    private val keySwitch = "autoModeSwitch"

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == keySwitch) {
            detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(keySwitch, false))
        } else if (key == keyThreshold) {
            detectorViewModel.autoModeThreshold.set(
                try {
                    sharedPreferences.getString(keyThreshold, "0").toInt()
                } catch (_: NumberFormatException) {
                    0
                })

            if (!sharedPreferences.getBoolean(keySwitch, false)) {
                break  // case 5
            }
        }

        if (sharedPreferences.getInt(keyThreshold, 0) != 0) {
            detectorViewModel.checkAutoMode()
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