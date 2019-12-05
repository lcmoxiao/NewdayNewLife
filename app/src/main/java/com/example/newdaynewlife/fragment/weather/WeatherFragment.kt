package com.example.newdaynewlife.fragment.weather

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import com.example.newdaynewlife.base.BaseFragment
import com.example.newdaynewlife.R
import kotlinx.android.synthetic.main.weather_fragment.*
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.*
import java.io.IOException

import kotlin.collections.ArrayList





class WeatherFragment : BaseFragment() {

    var data = ArrayList<WeatherItemData>()

    override fun getLayoutResId(): Int {
        return R.layout.weather_fragment
    }

    override fun init() {
        setDate()
        //使用http获得南岸当天的天气情况
        getWeather()
    }

    //用这个是因为子线程中不允许对UI进行更新
    var handler = Handler{
        when (it.what) {
            1-> {
                setWeatherInfo()
                true
            }else -> true
        }
    }

    private fun getWeather(){
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url("http://www.weather.com.cn/weather1d/101044000.shtml#input").build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("xx失败了", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                val matcher = Regex(""""1d":.*]]""").find(response.body!!.string())?.value
                val weatherList = Regex(""":\[".*?"]""").find(matcher!!)?.value
                val list3 = weatherList?.split(Regex("""","""))
                if (list3 != null) {
                    for (str in list3){
                        data.add(WeatherItemData(str,"",""))
                    }
                    handler.sendEmptyMessage(1)
                }
            }
        })
    }

    //设置今天的天气信息
    private fun setWeatherInfo()
    {
        weatherRecycleListView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        weatherRecycleListView.adapter = WeatherAdapter(data)
    }

    //设置今天的年月日
    @SuppressLint("SetTextI18n")
    private fun setDate()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        tvdate.text = String.format(resources.getString(R.string.current_time,year,month,day))
    }
}