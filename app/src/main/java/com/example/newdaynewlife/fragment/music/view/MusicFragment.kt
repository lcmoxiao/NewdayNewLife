package com.example.newdaynewlife.fragment.music.view

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.Context.BIND_AUTO_CREATE
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import com.example.newdaynewlife.base.BaseFragment
import com.example.newdaynewlife.R
import com.example.newdaynewlife.fragment.music.model.MusicLoad
import com.example.newdaynewlife.fragment.music.present.MusicService
import kotlinx.android.synthetic.main.music_fragment.*
import java.lang.Exception


class MusicFragment : BaseFragment()
{
    //在服务被绑定的时候进行绑定
    lateinit var musicControl: MusicService.MyBinder
    private lateinit var conn: MyConnection
    private lateinit var receiver:BroadcastReceiver
    private lateinit var notificationManager:NotificationManager
    private lateinit var notificationbuilder: NotificationCompat.Builder
    private lateinit var notification: Notification
    private val UPDATE_PROGRESS = 0
    private val musicLoad =
    @SuppressLint("StaticFieldLeak")
    object :MusicLoad(){
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            downseekbar.progress=values[0]!!
            btdown.text = values[0]!!.toString()
        }
        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if(result!!) {//绑定并启动音乐服务
                activity!!.bindService(Intent(activity, MusicService().javaClass), MyConnection(), BIND_AUTO_CREATE)
                //初始化后台通知栏
                initNotification()
                // 初始化跨通知栏和fragment通信的receiver
                initReceiver()
                btdown.text = "下载成功"
            }
            else btdown.text = "今天没有音乐"
        }
    }

    override fun init() {
        super.init()
        initText()
        //初始化按钮
        initButton()
        musicLoad.execute()
    }

    private fun initText()
    {
        btdown.text ="开始下载"
        btmusicplay.text="等待中"
    }


    private fun initReceiver()
    {
        receiver = object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                musicControl.play()
                updatePlayText()
            }
        }
        val shotIntentFilter = IntentFilter()
        shotIntentFilter.addAction("bt")
        activity!!.registerReceiver(receiver, shotIntentFilter)
    }

    private fun initButton()
    {
        musicplayseekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    musicControl.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        btmusicplay.setOnClickListener {
            musicControl.play()
            updatePlayText()
        }
        btdown.setOnClickListener {
            musicLoad.isCanceled=true
            musicLoad.cancel(true)
        }

    }

    //这是一个会自己给自己发信息的无限循环handler
    private var handler = Handler {
        when (it.what) {
            UPDATE_PROGRESS ->
            {
                try {
                    updateProgress()
                }catch (e:Exception){
                    Log.e("xx","还在加载")
                }
                true
            }
            else -> true
        }
    }

    //更新进度条
    private fun updateProgress() {
        musicplayseekbar.progress =  musicControl.getCurrenPostion()
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500)
    }

    //初始化MyConnection并绑定musicControl
    inner class MyConnection: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //获得service中的MyBinder
            musicControl =  service as MusicService.MyBinder
            //设置进度条的最大值
            musicplayseekbar.max = musicControl.getDuration()
            //设置进度条的进度
            musicplayseekbar.progress = musicControl.getCurrenPostion()
        }
    }

    private fun initNotification() {
        val mcontext = context!!
        notificationManager = mcontext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mChannel = NotificationChannel( "1", "portable play", NotificationManager.IMPORTANCE_LOW)
        mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(mChannel)
        val remoteView = RemoteViews(context?.packageName,R.layout.musicnotification)
        remoteView.setOnClickPendingIntent(R.id.nobtmusic,PendingIntent.getBroadcast(mcontext, 0,  Intent("bt"), PendingIntent.FLAG_CANCEL_CURRENT))
        notification = NotificationCompat.Builder(mcontext,"1")
            .setPriority(2)
            .setSmallIcon(R.mipmap.app)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContent(remoteView).build()
        notificationManager.notify(1,notification)
    }

    private fun notificationText(str:String)
    {
        val remoteView = RemoteViews(activity?.packageName,R.layout.musicnotification)
        remoteView.setOnClickPendingIntent(R.id.nobtmusic,PendingIntent.getBroadcast(context, 0,  Intent("bt"), PendingIntent.FLAG_CANCEL_CURRENT))
        remoteView.setTextViewText(R.id.nobtmusic,str)
        notificationbuilder.setContent(remoteView)
        notification = notificationbuilder.build()
        notificationManager.notify(1,notification)
    }


    //更新按钮的文字
    private fun updatePlayText() {
        if (musicControl.isPlaying()) {
            btmusicplay.text = "暂停"
            notificationText("暂停")
            handler.sendEmptyMessage(UPDATE_PROGRESS)
        } else {
            btmusicplay.text = "播放"
            notificationText("播放")
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

    override fun onStop() {
        super.onStop()
        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        activity!!.unregisterReceiver(receiver)
        activity!!.unbindService(conn)
        super.onDestroy()
    }

}