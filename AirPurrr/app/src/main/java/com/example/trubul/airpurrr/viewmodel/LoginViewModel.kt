package com.example.trubul.airpurrr.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var isEmailError = ObservableBoolean()
    var isPasswordError = ObservableBoolean()

    fun isEmailValid(login: String) {
        isEmailError.set(login.isBlank())
    }

    fun isPasswordValid(password: String) {
        isPasswordError.set(password.isBlank())
    }


    fun onLoginButtonClick() {
//        isEmailError.notifyChange()
//        isPasswordError.notifyChange()

//        showProgressDialog()
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
//            if (task.isSuccessful) {
//                PreferenceManager.getDefaultSharedPreferences(this).edit {
//                    putString(getString(R.string.login_pref_email), LoginHelper.sha512Hash(email))
//                    putString(getString(R.string.login_pref_password), LoginHelper.sha512Hash(password))
//                }
//
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            } else {
//                Snackbar.make(layout_login, R.string.login_message_error_auth, Snackbar.LENGTH_SHORT).show()
//            }
//
//            hideProgressDialog()
//        }
    }

}