package com.krzdabrowski.airpurrr.view

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val thresholdPreference by lazy { findPreference<EditTextPreference>(keyThreshold) }
    private val keyThreshold = "autoModeThreshold"
    private val keySwitch = "autoModeSwitch"

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        Timber.d("editText threshold value is: ${sharedPreferences.getString(keyThreshold, "")}")

        if (key == keySwitch) {
            detectorViewModel.autoModeSwitch.set(sharedPreferences.getBoolean(keySwitch, false))
        } else if (key == keyThreshold) {
            detectorViewModel.autoModeThreshold.set(
                try {
                    sharedPreferences.getString(keyThreshold, "").toInt()
                } catch (_: NumberFormatException) {
                    0
                })
        }

        /* to use auto-mode:
         * threshold MUST NOT be empty (must be set)
         * threshold MUST NOT be 0 / defValue
         * it should not use edge-case when: threshold has been clicked and set && switch is off (case #5)
         * it should block edge-case when: threshold has been clicked and NOT set && switch is on (case #8)
         */
        if (!sharedPreferences.getString(keyThreshold, "").isNullOrEmpty() &&
                !(key == keyThreshold && !sharedPreferences.getBoolean(keySwitch, false))) {
            detectorViewModel.checkAutoMode()
        } else if (sharedPreferences.getString(keyThreshold, "").isNullOrEmpty() &&
                key == keyThreshold && sharedPreferences.getBoolean(keySwitch, false)) {
            Toast.makeText(context, "It's bad case #8!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        thresholdPreference?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            Timber.d("onCreatePreference threshold value is: $text")
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