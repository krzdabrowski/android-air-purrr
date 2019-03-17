package com.example.trubul.airpurrr.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.hardware.fingerprint.FingerprintManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager

import androidx.core.app.ActivityCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.trubul.airpurrr.LoginHelper
import com.example.trubul.airpurrr.R

import com.example.trubul.airpurrr.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.partial_login_manual.view.*
import timber.log.Timber.DebugTree
import timber.log.Timber

class LoginActivity : BaseActivity(),
        LoginHelper.FingerprintCallback {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginHelper: LoginHelper
    private lateinit var mInputMethodManager: InputMethodManager
    private lateinit var mSharedPreferences: SharedPreferences
    private var mHashedEmail: String? = null

    private val email: String
        get() = partial_login_manual.input_email.text!!.toString().trim { it <= ' ' }
    private val password: String
        get() = partial_login_manual.input_password.text!!.toString().trim { it <= ' ' }

    private val isFingerprintPermissionGranted: Boolean
        get() = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED

    private val isInternetConnection: Boolean
        get() {
            var valid = true

            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo == null || !networkInfo.isConnected || networkInfo.type != ConnectivityManager.TYPE_WIFI && networkInfo.type != ConnectivityManager.TYPE_MOBILE) {
                valid = false
                Toast.makeText(this, R.string.login_message_error_no_internet, Toast.LENGTH_SHORT).show()
            }

            return valid
        }

    private val mShowKeyboardRunnable = Runnable { mInputMethodManager.showSoftInput(partial_login_manual.input_email, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
        Timber.plant(DebugTree())

        val keyguardManager = getSystemService(KeyguardManager::class.java)
        val fingerprintManager = getSystemService(FingerprintManager::class.java)
        mLoginHelper = LoginHelper(fingerprintManager, this)
        mInputMethodManager = getSystemService(InputMethodManager::class.java)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mHashedEmail = mSharedPreferences.getString(SAVED_HASH_EMAIL_KEY, null)

        partial_login_manual.btn_login.setOnClickListener { manualLogin(email, password) }

        // If phone has fingerprint reader and user has granted permission for an app
        if (mLoginHelper.isFingerprintAuthAvailable && isFingerprintPermissionGranted) {

            if (!keyguardManager.isKeyguardSecure) {  // show a message that the user hasn't set up a fingerprint or lock screen
                Toast.makeText(this, R.string.login_message_error_no_secure_screen, Toast.LENGTH_LONG).show()
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {  // this happens when no fingerprints are registered
                Toast.makeText(this, R.string.login_message_error_no_saved_fingerprint, Toast.LENGTH_LONG).show()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (mLoginHelper.isFingerprintAuthAvailable && isFingerprintPermissionGranted && mHashedEmail != null) {
            partial_login_manual.visibility = View.GONE
            partial_login_fingerprint.visibility = View.VISIBLE
            mLoginHelper.startListening()
        } else {
            activateKeyboard()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (mLoginHelper.isFingerprintAuthAvailable && isFingerprintPermissionGranted) {
            mLoginHelper.stopListening()
        }
    }

    private fun isFormFilled(email: String, password: String): Boolean {
        var valid = true

        if (TextUtils.isEmpty(email)) {
            valid = false
            partial_login_manual.input_email.error = getString(R.string.login_message_error_empty_field)
        } else {
            partial_login_manual.input_email.error = null
        }

        if (TextUtils.isEmpty(password)) {
            valid = false
            partial_login_manual.input_password.error = getString(R.string.login_message_error_empty_field)
        } else {
            partial_login_manual.input_password.error = null
        }

        return valid
    }

    private fun manualLogin(email: String, password: String) {
        if (!isInternetConnection || !isFormFilled(email, password)) {
            return
        }

        showProgressDialog()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                val editor = mSharedPreferences.edit()

                mHashedEmail = LoginHelper.sha512Hash(email)
                val hashedPassword = LoginHelper.sha512Hash(password)
                editor.putString(SAVED_HASH_EMAIL_KEY, mHashedEmail)
                editor.putString(SAVED_HASH_PASSWORD_KEY, hashedPassword)
                editor.apply()

                startActivity(intent)
            } else {
                Toast.makeText(this@LoginActivity, R.string.login_message_error_auth, Toast.LENGTH_SHORT).show()
            }

            hideProgressDialog()
        }
    }

    private fun activateKeyboard() {
        partial_login_manual.input_password.requestFocus()
        partial_login_manual.input_password.postDelayed(mShowKeyboardRunnable, 500)  // show the keyboard
        mLoginHelper.stopListening()
    }

    override fun onError() {
        Timber.d("onError: ")
        activateKeyboard()
    }

    override fun onHelp(helpString: CharSequence) {
        Timber.d("onHelp: ")
        Toast.makeText(this, helpString, Toast.LENGTH_SHORT).show()
    }

    override fun onFailed() {
        Timber.d("onFailed: ")
    }

    override fun onAuthenticated() {
        Timber.d("onAuthenticated: ")
        showProgressDialog()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        hideProgressDialog()
    }

    companion object {
        const val SAVED_HASH_EMAIL_KEY = "login_email"
        const val SAVED_HASH_PASSWORD_KEY = "login_password"
    }
}
