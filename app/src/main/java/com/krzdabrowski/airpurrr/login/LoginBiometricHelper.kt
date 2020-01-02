package com.krzdabrowski.airpurrr.login

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import com.krzdabrowski.airpurrr.R

@TargetApi(Build.VERSION_CODES.P)
class LoginBiometricHelper(private val context: Context, private val callback: OnSuccessCallback) : BiometricPrompt.AuthenticationCallback() {

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        callback.onSuccess()
    }

    fun isFingerprintAvailable() = BiometricManager.from(context).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

    fun isPermissionGranted() = ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED

    fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.login_fingerprint_text))
                .setNegativeButtonText(context.getString(R.string.login_fingerprint_button_cancel))
                .build()
    }

    interface OnSuccessCallback {
        fun onSuccess()
    }
}