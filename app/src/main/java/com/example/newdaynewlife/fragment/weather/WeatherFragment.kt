package com.example.newdaynewlife.fragment.weather

import android.os.Handler
import android.util.Log
import com.example.newdaynewlife.base.BaseFragment
import com.example.newdaynewlife.R
import com.example.newdaynewlife.http.GetResponse
import com.example.newdaynewlife.http.myCallback
import kotlinx.android.synthetic.main.weather_fragment.*
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager

import kotlin.collections.ArrayList


class WeatherFragment : BaseFragment() {

    var data = ArrayList<WeatherItemData>()

    override fun getLayoutResId(): Int {
        return R.layout.weather_fragment
    }

    override fun init() {

        setdate()

        GetResponse.getWeather(object:myCallback
        {
            override fun onSuccess(result: String) {
                val matcher = Regex(""""1d":.*]]""").find(result)?.value
                //获得南岸当天的天气情况
                val weatherList = Regex(""":\[".*?"]""").find(matcher!!)?.value

                val list3 = weatherList?.split(Regex("""","""))

                if (list3 != null) {
                    for (str in list3){
                        data.add(WeatherItemData(str,"",""))
                    }
                    Log.e("xx2", "sendEmptyMessage")
                    handler.sendEmptyMessage(1)
                }
            }
        })
    }


    var handler = Handler{
        when (it.what) {
            1-> {
                setweatherinfo()
                true
            }else -> true
        }
    }

    fun setweatherinfo()
    {
        weatherRecycleListView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        Log.e("xx2", "get")
        weatherRecycleListView.adapter = WeatherAdapter(data)
    }


    fun setdate()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        tvdate.text = """${year}/${month}/${day}"""
    }
}