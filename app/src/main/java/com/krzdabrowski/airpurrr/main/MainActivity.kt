package com.krzdabrowski.airpurrr.main

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val detectorViewModel: DetectorViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            add(R.id.main_activity, MainFragment())
            addToBackStack(null)
        }
        onBackPressedDispatcher.addCallback { moveTaskToBack(true) }
    }
}
