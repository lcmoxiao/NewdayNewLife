package com.example.newdaynewlife.fragment

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import com.example.newdaynewlife.base.BaseFragment
import com.example.newdaynewlife.R
import com.example.newdaynewlife.service.MusicService
import kotlinx.android.synthetic.main.music_fragment.*
import java.lang.Exception

class MusicFragment : BaseFragment()
{

    lateinit var musicControl: MusicService.MyBinder
    lateinit var conn:MyConnection
    val UPDATE_PROGRESS = 0

    override fun init() {
        super.init()
        tvmusicname.text = getString(R.string.music)
        val intent = Intent(activity,MusicService().javaClass)
        conn = MyConnection()
        Log.e("xx","开启服务")
        activity!!.startService(intent)
        Log.e("xx","开始绑定")
        activity!!.bindService(intent, conn, BIND_AUTO_CREATE)
        Log.e("xx","绑定结束")
        seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    musicControl.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        btmusic.setOnClickListener {
            musicControl.play()
            updatePlayText()
        }
    }

    private var handler = Handler {
        when (it.arg1) {
            UPDATE_PROGRESS ->
            {
                try {
                    updateProgress()
                    Log.e("xx","更新进度条")
                }catch (e:Exception){
                    Log.e("xx","还在加载")
                }
                true
            }
            else -> true
        }
    }

    inner class MyConnection: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //获得service中的MyBinder
            musicControl =  service as MusicService.MyBinder
            //更新按钮的文字
            updatePlayText()
            //设置进度条的最大值
            seekbar.max = musicControl.getDuration()
            //设置进度条的进度
            seekbar.progress = musicControl.getCurrenPostion()
        }
    }

    //更新进度条
    private fun updateProgress() {
        val currenPosition = musicControl.getCurrenPostion()
        seekbar.progress = currenPosition
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500)
    }

    //更新按钮的文字
    private fun updatePlayText() {
        if (musicControl.isPlaying()) {
            btmusic.text = "暂停"
            handler.sendEmptyMessage(UPDATE_PROGRESS)
        } else {
            btmusic.text = "播放"
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.music_fragment
    }
    
    override fun onPause() {
        Log.e("xx","onPause")
        super.onPause()
    }
    override fun onResume() {
        Log.e("xx","onResume")
        super.onResume()
        //进入到界面后开始更新进度条
        handler.sendEmptyMessage(UPDATE_PROGRESS)
    }

    override fun onDestroy() {
        activity!!.unbindService(conn)
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null)
    }
}