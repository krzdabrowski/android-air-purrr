package com.krzdabrowski.airpurrr.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.favre.lib.armadillo.Armadillo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.databinding.FragmentLoginBinding
import com.krzdabrowski.airpurrr.helper.PREFS_LOGIN_KEY_CREDENTIALS
import com.krzdabrowski.airpurrr.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var credentialPrefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        credentialPrefs = Armadillo.create(context, PREFS_LOGIN_KEY_CREDENTIALS)
                .encryptionFingerprint(context)
                .build()

        binding.loginVm = loginViewModel
        binding.isLoggingIn = false

        loginViewModel.email.observe(this, Observer { email -> loginViewModel.isEmailValid(email) })
        loginViewModel.password.observe(this, Observer { password -> loginViewModel.isPasswordValid(password) })
        loginViewModel.isFormValid.observe(this, Observer { manualLogin() })
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

                val directions = LoginFragmentDirections.navigateToMainScreen()  // add hashed login and password as args here
                findNavController().navigate(directions)
            } else {
                binding.isLoggingIn = false
                Snackbar.make(view!!, R.string.login_message_error_auth, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}