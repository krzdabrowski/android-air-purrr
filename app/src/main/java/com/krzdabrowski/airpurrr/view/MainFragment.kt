package com.krzdabrowski.airpurrr.view

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.helper.PurifierHelper
import com.krzdabrowski.airpurrr.viewmodel.DetectorViewModel
import kotlinx.android.synthetic.main.fragment_data_current.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {
    private val detectorViewModel: DetectorViewModel by viewModel()
    private val purifierHelper: PurifierHelper by inject()

    private var hashedEmail: String? = ""
    private var hashedPassword: String? = ""
    private var manualModeState = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_pager.adapter = PagerAdapter(context!!, childFragmentManager)
        tab_layout.setupWithViewPager(view_pager)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        hashedEmail = sharedPreferences.getString(getString(R.string.login_pref_email), "")
        hashedPassword = sharedPreferences.getString(getString(R.string.login_pref_password), "")
    }

    private fun onManualModeClick(email: String, password: String, state: Boolean) {
        detectorViewModel.getLiveData().observe(this, Observer { workstateValue ->
            manualModeState = purifierHelper.getPurifierState(workstateValue, email, password, state, swipe_refresh) }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_manual_mode -> {
                onManualModeClick(hashedEmail!!, hashedPassword!!, manualModeState)
                true
            }
            R.id.mnu_settings -> {
                findNavController().navigate(R.id.navigate_to_settings_screen)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}