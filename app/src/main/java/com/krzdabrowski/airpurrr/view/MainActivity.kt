package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krzdabrowski.airpurrr.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
