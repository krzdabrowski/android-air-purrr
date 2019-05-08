package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.di.helperModule
import com.krzdabrowski.airpurrr.di.networkModule
import com.krzdabrowski.airpurrr.di.repositoryModule
import com.krzdabrowski.airpurrr.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initLibs()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }

    private fun initLibs() {
        Timber.plant(DebugTree())
        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger()
            androidContext(this@LoginActivity)
            modules(listOf(networkModule, helperModule, repositoryModule, viewModelModule))
        }
    }
}
