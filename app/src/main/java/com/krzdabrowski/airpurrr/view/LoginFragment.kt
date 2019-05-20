package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.favre.lib.armadillo.Armadillo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.databinding.FragmentLoginBinding
import com.krzdabrowski.airpurrr.helper.BiometricHelper
import com.krzdabrowski.airpurrr.helper.PREFS_LOGIN_KEY_CREDENTIALS
import com.krzdabrowski.airpurrr.viewmodel.LoginViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModel()
    private val biometricHelper: BiometricHelper by inject()
    private val credentialPrefs by lazy { Armadillo.create(context, PREFS_LOGIN_KEY_CREDENTIALS).encryptionFingerprint(context).build() }
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.vm = loginViewModel
        binding.isLoggingIn = false

        loginViewModel.email.observe(this, Observer { email -> loginViewModel.isEmailValid(email) })
        loginViewModel.password.observe(this, Observer { password -> loginViewModel.isPasswordValid(password) })
        loginViewModel.isFormValid.observe(this, Observer { manualLogin() })

        if (credentialPrefs.contains(getString(R.string.login_pref_email))) {
            showFingerprintPrompt()
        }
    }

    // bug in biometric-alpha04: https://issuetracker.google.com/issues/131980596
    private fun showFingerprintPrompt() {
        val biometricPrompt = BiometricPrompt(activity!!, LoginActivity.MainExecutor(), biometricHelper)
        biometricPrompt.authenticate(biometricHelper.getPromptInfo())
    }

    private fun manualLogin() {
        binding.isLoggingIn = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(loginViewModel.email.value!!, loginViewModel.password.value!!).addOnCompleteListener(activity!!) { task ->
            if (task.isSuccessful) {
                with (credentialPrefs.edit()) {
                    putString(getString(R.string.login_pref_email), loginViewModel.email.value)
                    putString(getString(R.string.login_pref_password), loginViewModel.password.value)
                    apply()
                }

                val directions = LoginFragmentDirections.navigateToMainScreen()
                findNavController().navigate(directions)
            } else {
                binding.isLoggingIn = false
                Snackbar.make(view!!, R.string.login_message_error_auth, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}