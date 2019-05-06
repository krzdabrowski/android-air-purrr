package com.krzdabrowski.airpurrr.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.krzdabrowski.airpurrr.R

private const val TAB_SIZE = 2

class PagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
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

    override fun getCount() = TAB_SIZE
}