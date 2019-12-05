package com.example.newdaynewlife.fragment.music.model

import android.os.AsyncTask
import android.os.Environment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile

class MusicLoad :AsyncTask<String,Int,Boolean>(){

    private var lastProgress: Int = 0
    var isCanceled=false
    var isPaused=false

    override fun doInBackground(vararg params: String?): Boolean {

        val savedFile:RandomAccessFile
        val bytestream: InputStream
        var downloadLength: Long = 0   //记录已经下载的文件长度
        val downloadUrl = "https://upload-images.jianshu.io/upload_images/944365-153fb37764704129.png?imageMogr2/auto-orient/strip|imageView2/2/w/1130"
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val fileName = "1.png"


        val file= File(directory+fileName)
        //如果文件存在的话，得到文件的大小
        if(file.exists()) downloadLength = file.length()
        //得到下载内容的大小
        val contentLength = getContentLength(downloadUrl)
        //已下载字节和文件总字节相等，说明已经下载完成了
        if(contentLength== (-1).toLong()) return false
        else if(contentLength==downloadLength)return true

        savedFile=  RandomAccessFile(file,"rw")
        savedFile.seek(downloadLength)//跳过已经下载的字节

        //开始下载
        val client = OkHttpClient()
        /**
         * HTTP请求是有一个Header的，里面有个Range属性是定义下载区域的，它接收的值是一个区间范围，
         * 比如：Range:bytes=0-10000。这样我们就可以按照一定的规则，将一个大文件拆分为若干很小的部分，
         * 然后分批次的下载，每个小块下载完成之后，再合并到文件中；这样即使下载中断了，重新下载时，
         * 也可以通过文件的字节长度来判断下载的起始点，然后重启断点续传的过程，直到最后完成下载过程。
         */
        val request = Request.Builder().addHeader("RANGE", "bytes=$downloadLength-")  //断点续传要用到的，指示下载的区间
            .url(downloadUrl).build()
            val response=client.newCall(request).execute()
            bytestream = response.body!!.byteStream()
        try {
            val b = ByteArray(1024)
            var total=0
            val len =bytestream.read(b)
            while((len)!=-1){
                if(isCanceled){
                    return false
                }else if(isPaused){
                    return false
                }else {
                    total+=len
                    savedFile.write(b,0,len);
                    //计算已经下载的百分比
                    val progress= ((total+downloadLength)*100/contentLength).toInt()
                    //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                    //可以调用publishProgress()方法完成。
                    publishProgress(progress)
                }
            }
            response.body!!.close()
            return true
        } catch (e:IOException) {
            e.printStackTrace()
        }finally {
            try{
                bytestream.close()
                savedFile.close()
                file.delete()
            }catch (e:Exception ){
                e.printStackTrace()
            }
        }
        return false
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

    override fun onProgressUpdate(vararg values: Int?) {
        if(values[0]!! >lastProgress){
            lastProgress = values[0]!!
        }
    }

}