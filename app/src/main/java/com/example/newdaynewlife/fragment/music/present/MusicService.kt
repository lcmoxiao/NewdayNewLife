package com.example.newdaynewlife.fragment.music.present

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.newdaynewlife.R
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File

class MusicService : Service() {

    var musicPlayer = MediaPlayer()

    override fun onCreate() {
        val uri =Uri.parse("android.resource://"+ packageName + File.separator+ R.raw.music)
        musicPlayer.setDataSource(baseContext,uri)
        musicPlayer.prepare()
        musicPlayer.start()
    }

    override fun onBind(intent: Intent): IBinder? {
        return MyBinder()
    }

    override fun onDestroy() {
        musicPlayer.stop()
        super.onDestroy()
    }

     inner class MyBinder : Binder(){
         fun getDuration(): Int {
             return musicPlayer.duration
         }

         fun isPlaying(): Boolean {
                 return musicPlayer.isPlaying
             }
         //播放或暂停歌曲
         fun play() {
             if (musicPlayer.isPlaying) {
                 musicPlayer.pause()
             } else {
                 musicPlayer.start()
             }
         }

         //返回歌曲目前的进度，单位为毫秒
         fun getCurrenPostion():Int{
             return musicPlayer.currentPosition
         }

         //设置歌曲播放的进度，单位为毫秒
         fun seekTo(mesc:Int){
             musicPlayer.seekTo(mesc)
         }



    }

}