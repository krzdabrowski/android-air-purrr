package com.example.trubul.airpurrr.helper

import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import timber.log.Timber

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal class LoginHelper(private val mFingerprintManager: FingerprintManager, private val mCallback: FingerprintCallback) : FingerprintManager.AuthenticationCallback() {
    private var mCancellationSignal: CancellationSignal? = null
    private var mSelfCancelled: Boolean = false

    val isFingerprintAuthAvailable: Boolean
        get() = mFingerprintManager.isHardwareDetected && mFingerprintManager.hasEnrolledFingerprints()

    internal interface FingerprintCallback {
        fun onError()
        fun onHelp(helpString: CharSequence)
        fun onFailed()
        fun onAuthenticated()
    }

    fun startListening() {
        if (!isFingerprintAuthAvailable) {
            return
        }
        mCancellationSignal = CancellationSignal()
        mSelfCancelled = false
        mFingerprintManager.authenticate(null, mCancellationSignal, 0 /* flags */, this, null)
    }

    fun stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true
            mCancellationSignal!!.cancel()
            mCancellationSignal = null
        }
    }

    // FingerprintManager.AuthenticationCallback
    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        Timber.d("onAuthenticationError: ")
        if (!mSelfCancelled) {
            mCallback.onError()
        }
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        Timber.d("onAuthenticationHelp: ")
        mCallback.onHelp(helpString)
    }

    override fun onAuthenticationFailed() {
        Timber.d("onAuthenticationFailed: ")
        mCallback.onFailed()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        Timber.d("onAuthenticationSucceeded: ")
        mCallback.onAuthenticated()
    }

    companion object {
        fun sha512Hash(toHash: String): String? {
            var hash: String? = null
            try {
                val digest = MessageDigest.getInstance("SHA-512")
                var bytes = toHash.toByteArray(charset("UTF-8"))
                digest.update(bytes, 0, bytes.size)
                bytes = digest.digest()

                hash = bytesToHex(bytes)  // this is ~55x faster than looping and String.formating()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return hash
        }

        // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
        private val hexArray = "0123456789ABCDEF".toCharArray()

        private fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }
}
