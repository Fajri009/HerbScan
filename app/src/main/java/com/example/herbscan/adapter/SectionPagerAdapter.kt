package com.example.herbscan.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.herbscan.ui.detail.tab.BiodataFragment
import com.example.herbscan.ui.detail.description.DescriptionFragment

class SectionPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = BiodataFragment()
            1 -> fragment = DescriptionFragment()
        }

        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}