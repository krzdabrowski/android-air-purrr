package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.text.InputType
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

//        supportFragmentManager.transaction { replace(R.id.settings, SettingsFragment()) }
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

}