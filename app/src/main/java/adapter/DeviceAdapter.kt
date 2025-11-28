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

class DeviceAdapter(private val context: Context) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<Device>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_device_status, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int = devices.size

    fun submitList(newDevices: List<Device>) {
        devices.clear()
        devices.addAll(newDevices)
        notifyDataSetChanged()
    }

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        private val deviceId: TextView = itemView.findViewById(R.id.deviceId)
        private val deviceStatus: TextView = itemView.findViewById(R.id.deviceStatus)
        private val lastReading: TextView = itemView.findViewById(R.id.lastReading)

        fun bind(device: Device) {
            deviceName.text = device.name
            deviceId.text = device.deviceId
            lastReading.text = "Last Reading: ${device.lastReading}"

            if (device.isOnline) {
                deviceStatus.text = "CONNECTED"
                deviceStatus.setBackgroundResource(R.drawable.status_connected_bg)
            } else {
                deviceStatus.text = "DISCONNECTED"
                deviceStatus.setBackgroundResource(R.drawable.status_disconnected_bg)
            }
        }
    }
}
