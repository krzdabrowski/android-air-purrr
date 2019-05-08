package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.krzdabrowski.airpurrr.R

class MainActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(R.id.main_fragment_host) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(navController)
        addOnBackPressedCallback {
            if (!navController.navigateUp()) moveTaskToBack(true)
            true
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
