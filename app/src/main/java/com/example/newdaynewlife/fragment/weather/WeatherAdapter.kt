package com.example.newdaynewlife.fragment.weather

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newdaynewlife.R
import com.example.newdaynewlife.fragment.weather.WeatherAdapter.MyHolder
import android.view.LayoutInflater


class WeatherAdapter(WeatherItemDatas:ArrayList<WeatherItemData>): RecyclerView.Adapter<MyHolder>() {

    private var data:ArrayList<WeatherItemData> = WeatherItemDatas

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        //将我们自定义的item布局R.layout.item_one转换为View
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        //返回这个MyHolder实体
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.tvdata.text = data[position].data
        holder.tvtemperature.text = data[position].temperature
        holder.tvwind.text = data[position].wind
    }


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvdata:TextView = itemView.findViewById(R.id.weather_item_data)
        val tvtemperature:TextView = itemView.findViewById(R.id.weather_item_temperature)
        val tvwind: TextView = itemView.findViewById(R.id.weather_item_wind)
    }

}