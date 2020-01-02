package com.krzdabrowski.airpurrr.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.common.EspressoIdlingResource
import com.krzdabrowski.airpurrr.databinding.ActivityLoginBinding
import com.krzdabrowski.airpurrr.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity(), LoginBiometricHelper.OnSuccessCallback {
    private val loginViewModel: LoginViewModel by viewModel()
    private val biometricHelper by lazy { LoginBiometricHelper(this, this) }
    private val sharedPrefs by lazy { getSharedPreferences(getString(R.string.login_key_sharedprefs), Context.MODE_PRIVATE) }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.isLoggingIn = false
        binding.vm = loginViewModel
        loginViewModel.email.observe(this) { email -> loginViewModel.isEmailValid(email) }
        loginViewModel.password.observe(this) { password -> loginViewModel.isPasswordValid(password) }
        loginViewModel.isFormValid.observe(this) { if (loginViewModel.isFormValid.value!!) manualLogin() }

        if (sharedPrefs.getBoolean(getString(R.string.login_pref_is_logged_in_before), false)) {
            fingerprintLogin()
        }
    }

    override fun onSuccess() = navigateToMainScreen()

    private fun navigateToMainScreen() = startActivity(Intent(this, MainActivity::class.java))

    private fun manualLogin() {
        binding.isLoggingIn = true
        // Espresso does not work well with coroutines yet. See
        // https://github.com/Kotlin/kotlinx.coroutines/issues/982
        EspressoIdlingResource.increment()  // set app as busy

        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(loginViewModel.email.value!!, loginViewModel.password.value!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        with (sharedPrefs.edit()) {
                            putBoolean(getString(R.string.login_pref_is_logged_in_before), true)
                            apply()
                        }
                        navigateToMainScreen()
                    } else {
                        binding.isLoggingIn = false
                        Snackbar.make(findViewById(android.R.id.content), R.string.login_error_auth, Snackbar.LENGTH_SHORT).show()
                    }

                    EspressoIdlingResource.decrement()  // set app as idle
                }
    }

    private fun fingerprintLogin() {
        EspressoIdlingResource.increment()  // set app as busy

        if (biometricHelper.isFingerprintAvailable()) {
            if (biometricHelper.isPermissionGranted()) {
                BiometricPrompt(this, Executors.newSingleThreadExecutor(), biometricHelper)
                        .authenticate(biometricHelper.getPromptInfo())

                EspressoIdlingResource.decrement()  // set app as idle
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.login_error_no_permission, Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.login_error_no_saved_fingerprint, Snackbar.LENGTH_SHORT).show()
        }
    }
}
