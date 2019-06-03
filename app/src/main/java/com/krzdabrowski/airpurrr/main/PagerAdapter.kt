package com.krzdabrowski.airpurrr.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.DataCurrentFragment
import com.krzdabrowski.airpurrr.main.forecast.DataForecastFragment

class PagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val viewPagerTabSize = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DataCurrentFragment()
            else -> DataForecastFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.main_tab_current)
            else -> context.getString(R.string.main_tab_forecast)
        }
    }

    override fun getCount() = viewPagerTabSize
}