package com.krzdabrowski.airpurrr.helper

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import timber.log.Timber

@TargetApi(Build.VERSION_CODES.P)
class BiometricHelper(private val context: Context) : BiometricPrompt.AuthenticationCallback() {
    fun isHardwareSupported() = FingerprintManagerCompat.from(context).isHardwareDetected
    fun isFingerprintAvailable() = FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    fun isPermissionGranted() = ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
    fun isFingerprintLogin() = isHardwareSupported() && isFingerprintAvailable() && isPermissionGranted()

    var biometricCallback: BiometricPrompt.AuthenticationCallback = this

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        Timber.d("onAuthError occurred")
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        Timber.d("onAuthSucceded occurred")
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Timber.d("onAuthFailed occurred")
    }

    fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                .setTitle("title")
                .setSubtitle("subtitle")
                .setDescription("desc")
                .setNegativeButtonText("cancel")
                .build()
    }
}