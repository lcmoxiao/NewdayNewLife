package com.example.newdaynewlife.fragment.music.model

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*

open class MusicLoad:AsyncTask<String,Int,Boolean>(){

    var isCanceled=false
    var isPaused=false
    lateinit var response:Response
    lateinit var byteStream:InputStream


    override fun doInBackground(vararg params: String?): Boolean {

        var downloadLength: Long = 0   //记录已经下载的文件长度
        val downloadUrl =
            "https://upload-images.jianshu.io/upload_images/944365-153fb37764704129.png?imageMogr2/auto-orient/strip|imageView2/2/w/1130"
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/"
        Log.e("xxmulu", directory)
        val fileName = "1.png"

        val file = File(directory + fileName)
        //如果文件存在的话，得到文件的大小
        if (file.exists()) downloadLength = file.length()
        //得到下载内容的大小
        val contentLength = getContentLength(downloadUrl)
        //已下载字节和文件总字节相等，说明已经下载完成了
        if (contentLength == (-1).toLong()) return false
        else if (contentLength == downloadLength) return true

        //开始下载
        val client = OkHttpClient()
        /**
         * HTTP请求是有一个Header的，里面有个Range属性是定义下载区域的，它接收的值是一个区间范围，
         * 比如：Range:bytes=0-10000。这样我们就可以按照一定的规则，将一个大文件拆分为若干很小的部分，
         * 然后分批次的下载，每个小块下载完成之后，再合并到文件中；这样即使下载中断了，重新下载时，
         * 也可以通过文件的字节长度来判断下载的起始点，然后重启断点续传的过程，直到最后完成下载过程。
         */
        while (file.length() != contentLength) {
            //获取当前文件大小
            downloadLength = file.length()
            Log.e("xx当前大小", downloadLength.toString())
            Log.e("xx需要的大小", contentLength.toString())
            //根据当前文件大小请求数据
            val request = Request.Builder()
                .addHeader("range", "bytes=$downloadLength-$contentLength")  //断点续传要用到的，指示下载的区间
                .url(downloadUrl).build()
            response = client.newCall(request).execute()
            byteStream = response.body!!.byteStream()

            try {
                while (true) {
                    Log.e("xx", "一次传输开始")
                    val b = ByteArray(1024)
                    val len = byteStream.read(b)
                    if(len == -1){
                        Log.e("xxbug", "空的")
                        break
                    }
                    Log.e("xx", "进入选择")
                    when {
                        isCanceled -> return false
                        isPaused -> return false
                        else -> {
                            Log.e("xx", "开始添加")
                            file.appendBytes(b.copyOf(len))
                            //计算已经下载的百分比
                            val progress = ((file.length() + downloadLength) * 100 / contentLength).toInt()
                            Log.e("xx进度", progress.toString())
                            Log.e("xxnow", file.length().toString())
                            Log.e("xxall", len.toString())
                            publishProgress(progress)
                        }
                    }
                }
                Log.e("xxprogress", "一次传输结束")
                response.body!!.close()
            } catch (e: IOException) {
                Log.e("xx",  e.toString())
                e.printStackTrace()
            } finally {
                try {
                    byteStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return true
    }

    /**
     * 得到下载内容的大小
     * @param downloadUrl
     * @return
     */
    private fun getContentLength(downloadUrl:String ):Long{
        val client= OkHttpClient()
        val request=Request.Builder().url(downloadUrl).build();
        try {
            val response=client.newCall(request).execute()
            if(response.isSuccessful){
                val contentLength = response.body!!.contentLength()
                response.body!!.close()
                return contentLength
            }
        } catch ( e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

}