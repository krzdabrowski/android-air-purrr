package com.krzdabrowski.airpurrr.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.databinding.FragmentLoginBinding
import com.krzdabrowski.airpurrr.helper.PERMISSION_REQUEST_CODE_LOCATION
import com.krzdabrowski.airpurrr.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.loginVm = loginViewModel
        binding.isLoggingIn = false

        loginViewModel.email.observe(this, Observer { email -> loginViewModel.isEmailValid(email) })
        loginViewModel.password.observe(this, Observer { password -> loginViewModel.isPasswordValid(password) })
        loginViewModel.isFormValid.observe(this, Observer { manualLogin() })

        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    Toast.makeText(activity, "Denied, getting default location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_LOCATION)
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Toast.makeText(activity, "Last location is: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Location not available, getting default one, sorry", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun manualLogin() {
        binding.isLoggingIn = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(loginViewModel.email.value!!, loginViewModel.password.value!!).addOnCompleteListener(activity!!) { task ->
            if (task.isSuccessful) {
//                PreferenceManager.getDefaultSharedPreferences(this).edit {
//                    putString(getString(R.string.login_pref_email), LoginHelper.sha512Hash(email))
//                    putString(getString(R.string.login_pref_password), LoginHelper.sha512Hash(password))
//                }
                val directions = LoginFragmentDirections.navigateToMainScreen()  // add hashed login and password as args here
                findNavController().navigate(directions)
            } else {
                binding.isLoggingIn = false
                Snackbar.make(view!!, R.string.login_message_error_auth, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}