package com.krzdabrowski.airpurrr.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.common.helperModule
import com.krzdabrowski.airpurrr.common.networkModule
import com.krzdabrowski.airpurrr.common.repositoryModule
import com.krzdabrowski.airpurrr.common.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.Executor

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

    // code from Retrofit library to get main executor in API <28 (for biometric callback purpose)
    class MainExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            handler.post(command)
        }
    }
}
