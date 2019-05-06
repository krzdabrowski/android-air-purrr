package com.krzdabrowski.airpurrr.view

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.helper.PurifierHelper
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_current_data.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val detectorViewModel: DetectorViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()

    private var hashedEmail: String? = ""
    private var hashedPassword: String? = ""
    private var manualModeState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter = PagerAdapter(this, supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        hashedEmail = sharedPreferences.getString(getString(R.string.login_pref_email), "")
        hashedPassword = sharedPreferences.getString(getString(R.string.login_pref_password), "")
    }

    private fun onManualModeClick(email: String, password: String, state: Boolean) {
        detectorViewModel.getLiveData().observe(this, Observer { workstateValue ->
            manualModeState = purifierHelper.getPurifierState(workstateValue, email, password, state, swipe_refresh) }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.mnu_manual_mode -> {
                onManualModeClick(hashedEmail!!, hashedPassword!!, manualModeState)
                true
            }
            R.id.mnu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
