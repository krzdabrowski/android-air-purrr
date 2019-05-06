package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import androidx.preference.EditTextPreference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import com.krzdabrowski.airpurrr.R

const val KEY_THRESHOLD = "autoModeThreshold"

// to read later: https://developer.android.com/guide/topics/ui/settings/use-saved-values
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.transaction { replace(R.id.settings, SettingsFragment()) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val thresholdPreference = findPreference<EditTextPreference>(KEY_THRESHOLD)
            thresholdPreference?.summaryProvider = SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (!TextUtils.isEmpty(text)) {
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
}