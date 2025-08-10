package com.example.ed_system.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.Device

class DeviceAdapter(
    private val devices: List<Device>,
    private val context: Context
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        val deviceId: TextView = itemView.findViewById(R.id.deviceId)
        val deviceStatus: TextView = itemView.findViewById(R.id.deviceStatus)
        val batteryLevel: TextView = itemView.findViewById(R.id.batteryLevel)
        val batteryIcon: ImageView = itemView.findViewById(R.id.batteryIcon)
        val signalStrength: TextView = itemView.findViewById(R.id.signalStrength)
        val lastReading: TextView = itemView.findViewById(R.id.lastReading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_card, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.deviceName.text = device.name
        holder.deviceId.text = device.id
        holder.deviceStatus.text = if (device.isConnected) "CONNECTED" else "DISCONNECTED"
        holder.deviceStatus.setBackgroundResource(
            if (device.isConnected) R.drawable.status_connected_bg else R.drawable.status_disconnected_bg
        )

        // Battery color
        val batteryColor = when {
            device.battery >= 60 -> ContextCompat.getColor(context, R.color.green)
            device.battery >= 30 -> ContextCompat.getColor(context, R.color.yellow)
            else -> ContextCompat.getColor(context, R.color.red)
        }
        holder.batteryLevel.text = "Battery ${device.battery}%"
        holder.batteryLevel.setTextColor(batteryColor)
        holder.batteryIcon.setColorFilter(batteryColor)

        // Signal
        holder.signalStrength.text = "Signal ${device.signal}%"
        holder.lastReading.text = "Last Reading: ${device.lastRead}"
    }

    override fun getItemCount(): Int = devices.size
}
