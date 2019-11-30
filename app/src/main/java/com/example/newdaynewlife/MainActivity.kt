package com.example.newdaynewlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.newdaynewlife.adapter.FragmentAdapter
import com.example.newdaynewlife.fragment.MusicFragment
import com.example.newdaynewlife.fragment.WeatherFragment


import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var vpAdapter: FragmentAdapter

    private fun initView() {
        //需要新的界面，就add新的fragment
        val fgs : ArrayList<Fragment> = ArrayList()
        fgs.add(MusicFragment())
        fgs.add(WeatherFragment())


        vpAdapter = FragmentAdapter(supportFragmentManager, fgs)
        vp.adapter = vpAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }





}
