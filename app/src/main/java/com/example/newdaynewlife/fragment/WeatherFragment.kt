package com.example.newdaynewlife.fragment

import com.example.newdaynewlife.base.BaseFragment
import com.example.newdaynewlife.R
import kotlinx.android.synthetic.main.weather_fragment.*


class WeatherFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.weather_fragment
    }
    override fun init() {
        super.init()
        tvweather.text = getString(R.string.weather)
    }
}