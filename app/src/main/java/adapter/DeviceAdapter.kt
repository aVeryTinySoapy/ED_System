package com.example.ed_system.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.Device

class DeviceAdapter(private val devices: List<Device>, private val context: Context) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_device_card, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int = devices.size

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        private val deviceId: TextView = itemView.findViewById(R.id.deviceId)
        private val deviceStatus: TextView = itemView.findViewById(R.id.deviceStatus)
        private val batteryIcon: ImageView = itemView.findViewById(R.id.batteryIcon)
        private val batteryLevel: TextView = itemView.findViewById(R.id.batteryLevel)
        private val signalIcon: ImageView = itemView.findViewById(R.id.signalIcon)
        private val signalStrength: TextView = itemView.findViewById(R.id.signalStrength)
        private val lastReading: TextView = itemView.findViewById(R.id.lastReading)

        fun bind(device: Device) {
            deviceName.text = device.name
            deviceId.text = device.deviceId
            batteryLevel.text = "Battery ${device.batteryLevel}%"
            signalStrength.text = "Signal ${device.signalStrength}%"
            lastReading.text = "Last Reading: ${device.lastReading}"

            // Set status text & background
            if (device.isOnline) {
                deviceStatus.text = "CONNECTED"
                deviceStatus.setBackgroundResource(R.drawable.status_connected_bg)
            } else {
                deviceStatus.text = "DISCONNECTED"
                deviceStatus.setBackgroundResource(R.drawable.status_disconnected_bg)
            }

            // Optionally: change battery color
            batteryIcon.setColorFilter(
                if (device.batteryLevel > 20) context.getColor(R.color.green)
                else context.getColor(R.color.red)
            )

            // Optionally: change signal icon tint
            signalIcon.setColorFilter(
                if (device.signalStrength > 50) context.getColor(R.color.black)
                else context.getColor(R.color.gray)
            )
        }
    }
}
