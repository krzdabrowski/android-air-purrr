package com.krzdabrowski.airpurrr.main.core

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.krzdabrowski.airpurrr.main.CurrentFragment
import com.krzdabrowski.airpurrr.main.ForecastFragment

internal const val CURRENT_SCREEN_POSITION = 0
private const val VIEW_PAGER_TAB_SIZE = 2

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            CURRENT_SCREEN_POSITION -> CurrentFragment()
            else -> ForecastFragment()
        }
    }

    override fun getItemCount() = VIEW_PAGER_TAB_SIZE
}