package com.example.trubul.airpurrr.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.databinding.ActivityLoginBinding
import com.example.trubul.airpurrr.di.helperModule
import com.example.trubul.airpurrr.di.networkModule
import com.example.trubul.airpurrr.di.repositoryModule
import com.example.trubul.airpurrr.di.viewModelModule
import com.example.trubul.airpurrr.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber.DebugTree
import timber.log.Timber

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by inject()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        Timber.plant(DebugTree())
        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger()
            androidContext(this@LoginActivity)
            modules(listOf(networkModule, helperModule, repositoryModule, viewModelModule))
        }

        binding.loginVm = loginViewModel
        binding.isLoggingIn = false

        loginViewModel.email.observe(this, Observer { email -> loginViewModel.isEmailValid(email) })
        loginViewModel.password.observe(this, Observer { password -> loginViewModel.isPasswordValid(password) })
        loginViewModel.isFormValid.observe(this, Observer { manualLogin() })
    }

    private fun manualLogin() {
        binding.isLoggingIn = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(loginViewModel.email.value!!, loginViewModel.password.value!!).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
//                PreferenceManager.getDefaultSharedPreferences(this).edit {
//                    putString(getString(R.string.login_pref_email), LoginHelper.sha512Hash(email))
//                    putString(getString(R.string.login_pref_password), LoginHelper.sha512Hash(password))
//                }
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                binding.isLoggingIn = false
                Snackbar.make(layout_login, R.string.login_message_error_auth, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
