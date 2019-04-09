package com.example.trubul.airpurrr.view

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager

import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.trubul.airpurrr.helper.LoginHelper
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
import kotlinx.android.synthetic.main.partial_login_manual.view.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber.DebugTree
import timber.log.Timber

class LoginActivity : BaseActivity() {
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
        binding.lifecycleOwner = this
        binding.loginVm = loginViewModel

        loginViewModel.email.observe(this, Observer { Toast.makeText(this, "email has changed", Toast.LENGTH_SHORT).show() })
        partial_login_manual.btn_login.setOnClickListener { manualLogin(loginViewModel.email.value!!, loginViewModel.password.value!!) }
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
        if (!isFormFilled(email, password)) {
            return
        }

        showProgressDialog()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                PreferenceManager.getDefaultSharedPreferences(this).edit {
                    putString(getString(R.string.login_pref_email), LoginHelper.sha512Hash(email))
                    putString(getString(R.string.login_pref_password), LoginHelper.sha512Hash(password))
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(layout_login, R.string.login_message_error_auth, Snackbar.LENGTH_SHORT).show()
            }

            hideProgressDialog()
        }
    }
}
