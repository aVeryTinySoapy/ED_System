package com.example.ed_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import model.RecentReading

class RecentReadingAdapter(
    private val readings: List<RecentReading>,
    private val deviceName: String
) : RecyclerView.Adapter<RecentReadingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDeviceName: TextView = view.findViewById(R.id.deviceName)
        val tvTimestamp: TextView = view.findViewById(R.id.timestamp)
        val tvDO: TextView = view.findViewById(R.id.doValue)
        val tvPH: TextView = view.findViewById(R.id.phValue)
        val tvTemp: TextView = view.findViewById(R.id.tempValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_reading, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = readings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = readings[position]
        holder.tvDeviceName.text = deviceName
        holder.tvTimestamp.text = item.timestamp
        holder.tvDO.text = "${item.doValue} mg/L"
        holder.tvPH.text = "${item.phValue}"
        holder.tvTemp.text = "${item.tempValue} °C"
    }
}
