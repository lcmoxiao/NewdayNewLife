package com.example.newdaynewlife.fragment.music

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.Context.BIND_AUTO_CREATE
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import com.example.newdaynewlife.MainActivity
import com.example.newdaynewlife.base.BaseFragment
import com.example.newdaynewlife.R
import kotlinx.android.synthetic.main.music_fragment.*
import kotlinx.android.synthetic.main.musicnotification.*
import java.lang.Exception


class MusicFragment : BaseFragment()
{

    lateinit var musicControl: MusicService.MyBinder
    lateinit var conn: MyConnection
    lateinit var receiver:BroadcastReceiver
    lateinit var notificationManager:NotificationManager
    lateinit var notificationbuilder: NotificationCompat.Builder
    lateinit var notification: Notification
    private val UPDATE_PROGRESS = 0


    override fun init() {
        super.init()

        initNotification()

        tvmusicname.text = getString(R.string.music)

        conn = MyConnection()
        activity!!.bindService(Intent(activity, MusicService().javaClass), conn, BIND_AUTO_CREATE)

        initReceiver()
        initButton()
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

    @SuppressLint("ObsoleteSdkInt")
    fun initNotification() {
        val mcontext = context!!
        notificationManager = mcontext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = "1"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "portable play"
            // 用户可以看到的通知渠道的描述
            val importance = NotificationManager.IMPORTANCE_LOW
            //注意Name和description不能为null或者""
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            //最后在notificationmanager中创建该通知渠道
            notificationManager.createNotificationChannel(mChannel)
        }
        val remoteView = RemoteViews(activity?.packageName,R.layout.musicnotification)
        remoteView.setOnClickPendingIntent(R.id.nobtmusic,PendingIntent.getBroadcast(mcontext, 0,  Intent("bt"), PendingIntent.FLAG_CANCEL_CURRENT))
        notificationbuilder = NotificationCompat.Builder(mcontext,id)
            .setPriority(2)
            .setSmallIcon(R.mipmap.app)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContent(remoteView)
        notification = notificationbuilder.build()
        notificationManager.notify(1,notification)
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
        val remoteView = RemoteViews(activity?.packageName,R.layout.musicnotification)
        remoteView.setOnClickPendingIntent(R.id.nobtmusic,PendingIntent.getBroadcast(context, 0,  Intent("bt"), PendingIntent.FLAG_CANCEL_CURRENT))
        if (musicControl.isPlaying()) {
            btmusic.text = "暂停"
            remoteView.setTextViewText(R.id.nobtmusic,"暂停")
            handler.sendEmptyMessage(UPDATE_PROGRESS)
        } else {
            btmusic.text = "播放"
            remoteView.setTextViewText(R.id.nobtmusic,"播放")
        }
        notificationbuilder.setContent(remoteView)
        notification = notificationbuilder.build()
        notificationManager.notify(1,notification)
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