package com.krzdabrowski.airpurrr.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.krzdabrowski.airpurrr.R
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
    }

    // code from Retrofit library to get main executor in API <28 (for biometric callback purpose)
    class MainExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            handler.post(command)
        }
    }
}
