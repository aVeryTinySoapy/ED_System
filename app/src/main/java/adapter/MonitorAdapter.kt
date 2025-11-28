package com.example.ed_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import model.Monitor

class MonitorAdapter(private val monitors: List<Monitor>) :
    RecyclerView.Adapter<MonitorAdapter.MonitorViewHolder>() {

    inner class MonitorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val siteName: TextView = view.findViewById(R.id.siteName)
        val siteTag: TextView = view.findViewById(R.id.siteTag)
        val deviceId: TextView = view.findViewById(R.id.deviceId)
        val lastUpdated: TextView = view.findViewById(R.id.lastUpdated)

        // param cards
        val doCard: View = view.findViewById(R.id.doCard)
        val phCard: View = view.findViewById(R.id.phCard)
        val tempCard: View = view.findViewById(R.id.tempCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitor_card, parent, false)
        return MonitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonitorViewHolder, position: Int) {
        val monitor = monitors[position]

        holder.siteName.text = monitor.siteName
        holder.siteTag.text = monitor.siteTag
        holder.deviceId.text = "Device: ${monitor.deviceId}"
        holder.lastUpdated.text = "Last updated: ${monitor.lastUpdated}"

        // --- Dissolved Oxygen ---
        holder.doCard.findViewById<TextView>(R.id.name).text = "Dissolved Oxygen"
        holder.doCard.findViewById<TextView>(R.id.value).text = monitor.doValue
        val doStatusTextView = holder.doCard.findViewById<TextView>(R.id.status)
        doStatusTextView.text = monitor.doStatus
        doStatusTextView.setTextColor(getStatusColor(monitor.doStatus))
        holder.doCard.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_oxygen)

        // --- pH ---
        holder.phCard.findViewById<TextView>(R.id.name).text = "pH Level"
        holder.phCard.findViewById<TextView>(R.id.value).text = monitor.phValue
        val phStatusTextView = holder.phCard.findViewById<TextView>(R.id.status)
        phStatusTextView.text = monitor.phStatus
        phStatusTextView.setTextColor(getStatusColor(monitor.phStatus))
        holder.phCard.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_ph)

        // --- Temperature ---
        holder.tempCard.findViewById<TextView>(R.id.name).text = "Temperature"
        holder.tempCard.findViewById<TextView>(R.id.value).text = monitor.tempValue
        val tempStatusTextView = holder.tempCard.findViewById<TextView>(R.id.status)
        tempStatusTextView.text = monitor.tempStatus
        tempStatusTextView.setTextColor(getStatusColor(monitor.tempStatus))
        holder.tempCard.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_temperature)
    }

    override fun getItemCount(): Int = monitors.size

    private fun getStatusColor(status: String): Int {
        return when (status.uppercase()) {
            "NORMAL" -> android.graphics.Color.parseColor("#4CAF50")   // Green
            "WARNING" -> android.graphics.Color.parseColor("#FF9800")  // Orange
            "DISCONNECTED" -> android.graphics.Color.parseColor("#9E9E9E") // Gray
            else -> android.graphics.Color.BLACK
        }
    }
}
