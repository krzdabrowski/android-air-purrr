package com.krzdabrowski.airpurrr.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.databinding.Observable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import at.favre.lib.armadillo.Armadillo
import com.google.android.material.snackbar.Snackbar
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.PurifierHelper
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NumberFormatException

class SettingsFragment : PreferenceFragmentCompat() {
    private val detectorViewModel: DetectorViewModel by sharedViewModel()
    private val purifierHelper: PurifierHelper by inject()

    private val credentialPrefs by lazy { Armadillo.create(context, getString(R.string.login_key_credentials)).encryptionFingerprint(context).build() }
    private val email by lazy { credentialPrefs.getString(getString(R.string.login_pref_email), null) }
    private val password by lazy { credentialPrefs.getString(getString(R.string.login_pref_password), null) }

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

    fun addStateCallback(owner: LifecycleOwner, rootView: View, email: String, password: String) {
        detectorViewModel.purifierObservableState.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                controlPurifier(owner, rootView, email, password, detectorViewModel.purifierState)
            }
        })
    }

    fun controlPurifier(owner: LifecycleOwner, rootView: View, email: String, password: String, state: Boolean) {
        detectorViewModel.getLiveData().observe(owner, Observer { workstateValue ->
            detectorViewModel.purifierState = purifierHelper.getPurifierState(workstateValue, email, password, state, rootView)
        })
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addStateCallback(this, view, email, password)
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