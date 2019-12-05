package com.example.newdaynewlife

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newdaynewlife.base.FragmentAdapter
import com.example.newdaynewlife.fragment.music.view.MusicFragment
import com.example.newdaynewlife.fragment.weather.WeatherFragment


import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager


class MainActivity : AppCompatActivity() {

    private lateinit var vpAdapter: FragmentAdapter

    private fun initView() {

        vpAdapter = FragmentAdapter(supportFragmentManager, listOf(MusicFragment(), WeatherFragment()))
        vp.adapter = vpAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPermission(READ_EXTERNAL_STORAGE)
        setContentView(R.layout.activity_main)
        initView()
    }


    //获取读写的权限
    private fun getPermission( permission: String) {
        when {
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            else -> if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 67)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 67)
            }
        }
    }



}
