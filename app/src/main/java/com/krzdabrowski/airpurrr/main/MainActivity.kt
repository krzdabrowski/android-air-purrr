package com.krzdabrowski.airpurrr.main

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.krzdabrowski.airpurrr.R

class MainActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(R.id.main_fragment_host) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(navController)
        onBackPressedDispatcher.addCallback { if (!navController.navigateUp()) moveTaskToBack(true) }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
