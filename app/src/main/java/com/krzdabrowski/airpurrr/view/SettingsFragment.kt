package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.krzdabrowski.airpurrr.R

// to read later: https://developer.android.com/guide/topics/ui/settings/use-saved-values
class SettingsFragment : PreferenceFragmentCompat() {
    private val keyThreshold = "autoModeThreshold"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val thresholdPreference = findPreference<EditTextPreference>(keyThreshold)
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
}