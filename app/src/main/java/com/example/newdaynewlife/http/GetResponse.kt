package com.example.newdaynewlife.http

import android.util.Log
import okhttp3.*
import java.io.IOException


class GetResponse {

    companion object{


        fun get(url: String) {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            val call = okHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("xx", "onFailure: $e")
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {

                    Log.d("xx", "onResponse: " + response.body!!.string())
                }
            })
        }

        fun getWeather(callback: myCallback){
            val okHttpClient = OkHttpClient()
            val request = Request.Builder().url("http://www.weather.com.cn/weather1d/101044000.shtml#input").build()
            val call = okHttpClient.newCall(request)

            call.enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("xx", e.toString())
                }
                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(response.body!!.string())
                }
            })




        }



    }





}