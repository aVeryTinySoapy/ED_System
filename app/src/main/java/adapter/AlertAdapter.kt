package com.example.ed_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.Alert

class AlertAdapter : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private val alerts = mutableListOf<Alert>()

    inner class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.alertIcon)
        val title: TextView = view.findViewById(R.id.alertTitle)
        val description: TextView = view.findViewById(R.id.alertDescription)
        val value: TextView = view.findViewById(R.id.alertValue)
        val threshold: TextView = view.findViewById(R.id.alertThreshold)
        val time: TextView = view.findViewById(R.id.alertTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert_card, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.icon.setImageResource(alert.iconRes)
        holder.title.text = alert.title
        holder.description.text = alert.description
        holder.value.text = alert.value
        holder.threshold.text = alert.threshold
        holder.time.text = alert.timeAgo

        // Color coding for status
        val color = when (alert.status.uppercase()) {
            "WARNING" -> android.R.color.holo_orange_dark
            "DANGER" -> android.R.color.holo_red_dark
            "NORMAL" -> android.R.color.holo_green_dark
            else -> android.R.color.darker_gray
        }
        holder.value.setTextColor(
            ContextCompat.getColor(holder.itemView.context, color)
        )
    }

    override fun getItemCount(): Int = alerts.size

    fun submitList(newAlerts: List<Alert>) {
        alerts.clear()
        alerts.addAll(newAlerts)
        notifyDataSetChanged()
    }
}
