package com.example.ed_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.Reading

class ReadingAdapter(private var readings: List<Reading>) :
    RecyclerView.Adapter<ReadingAdapter.ReadingViewHolder>() {

    inner class ReadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val siteName: TextView = view.findViewById(R.id.siteName)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
        val doValue: TextView = view.findViewById(R.id.doValue)
        val phValue: TextView = view.findViewById(R.id.phValue)
        val tempValue: TextView = view.findViewById(R.id.tempValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_reading, parent, false)
        return ReadingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReadingViewHolder, position: Int) {
        val reading = readings[position]
        holder.siteName.text = reading.siteName
        holder.timestamp.text = reading.timestamp
        holder.doValue.text = "DO ${reading.dissolvedOxygen}"
        holder.phValue.text = "pH ${reading.phLevel}"
        holder.tempValue.text = "Temp ${reading.temperature}"
    }

    override fun getItemCount(): Int = readings.size

    fun updateData(newReadings: List<Reading>) {
        readings = newReadings
        notifyDataSetChanged()
    }
}
