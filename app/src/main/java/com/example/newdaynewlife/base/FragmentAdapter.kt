package com.example.newdaynewlife.base


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class FragmentAdapter(fm: FragmentManager?, private val fgs: List<BaseFragment>) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fgs[position]
    }

    override fun getCount(): Int {
        return fgs.size
    }

}