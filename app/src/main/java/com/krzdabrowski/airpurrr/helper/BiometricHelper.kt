package com.krzdabrowski.airpurrr.helper

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.krzdabrowski.airpurrr.R

@TargetApi(Build.VERSION_CODES.P)
class BiometricHelper(private val context: Context, private val callback: OnSuccessCallback) : BiometricPrompt.AuthenticationCallback() {
    fun isFingerprintAvailable() = FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    fun isPermissionGranted() = ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED

    interface OnSuccessCallback {
        fun onSuccess()
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        callback.onSuccess()
    }

    fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.login_fingerprint_text))
                .setNegativeButtonText(context.getString(R.string.login_fingerprint_button_cancel))
                .build()
    }
}