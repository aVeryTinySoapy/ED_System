package com.example.ed_system

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.adapter.MonitorAdapter
import com.example.ed_system.model.Device
import model.Monitor
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MonitorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MonitorAdapter
    private lateinit var noDeviceText: TextView

    private val monitorList = mutableListOf<Monitor>()
    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())
    private val pollingInterval = 10000L // 10s

    private val pollingRunnable = object : Runnable {
        override fun run() {
            if (DeviceManager.deviceList.isEmpty()) {
                showNoDeviceMessage()
            } else {
                DeviceManager.deviceList.forEach { fetchDeviceData(it) }
            }
            handler.postDelayed(this, pollingInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_monitor, container, false)

        recyclerView = view.findViewById(R.id.monitorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MonitorAdapter(monitorList)
        recyclerView.adapter = adapter

        noDeviceText = TextView(requireContext()).apply {
            text = "No Device Added Yet. Please Add a Device in Settings."
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            visibility = View.GONE
        }
        (recyclerView.parent as ViewGroup).addView(noDeviceText, 0)

        startPolling()
        return view
    }

    private fun showNoDeviceMessage() {
        activity?.runOnUiThread {
            recyclerView.visibility = View.GONE
            noDeviceText.visibility = View.VISIBLE
            monitorList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun fetchDeviceData(device: Device) {
        val url = "https://api.thingspeak.com/channels/${device.channelId}/feeds.json?results=1"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                updateMonitor(device, "--", "--", "--", "DISCONNECTED", "DISCONNECTED", "DISCONNECTED", "--")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    updateMonitor(device, "--", "--", "--", "DISCONNECTED", "DISCONNECTED", "DISCONNECTED", "--")
                    return
                }

                try {
                    val feeds = JSONObject(body).optJSONArray("feeds")
                    if (feeds != null && feeds.length() > 0) {
                        val feed = feeds.getJSONObject(0)

                        val temp = feed.optString("field1", "--")
                        val doValue = feed.optString("field2", "--")
                        val phValue = feed.optString("field3", "--")
                        val timestamp = feed.optString("created_at", "--")

                        val tempStatus = getStatus(temp.toFloatOrNull(), 10f, 30f)
                        val doStatus = getStatus(doValue.toFloatOrNull(), 7f, 20f)
                        val phStatus = getStatus(phValue.toFloatOrNull(), 6.5f, 8.5f)

                        val formattedTime = formatTimestamp(timestamp)
                        updateMonitor(device, temp, doValue, phValue, tempStatus, doStatus, phStatus, formattedTime)
                    } else {
                        updateMonitor(device, "--", "--", "--", "DISCONNECTED", "DISCONNECTED", "DISCONNECTED", "--")
                    }
                } catch (e: Exception) {
                    updateMonitor(device, "--", "--", "--", "DISCONNECTED", "DISCONNECTED", "DISCONNECTED", "--")
                }
            }
        })
    }

    private fun updateMonitor(
        device: Device,
        temp: String,
        doValue: String,
        phValue: String,
        tempStatus: String,
        doStatus: String,
        phStatus: String,
        lastUpdated: String
    ) {
        val existing = monitorList.find { it.deviceId == device.deviceId }

        if (existing != null) {
            existing.tempValue = temp
            existing.tempStatus = tempStatus
            existing.doValue = doValue
            existing.doStatus = doStatus
            existing.phValue = phValue
            existing.phStatus = phStatus
            existing.lastUpdated = lastUpdated
        } else {
            monitorList.add(
                Monitor(
                    siteName = device.name,
                    siteTag = "DEVICE",
                    deviceId = device.deviceId,
                    lastUpdated = lastUpdated,
                    doValue = doValue,
                    doStatus = doStatus,
                    phValue = phValue,
                    phStatus = phStatus,
                    tempValue = temp,
                    tempStatus = tempStatus
                )
            )
        }

        activity?.runOnUiThread {
            recyclerView.visibility = View.VISIBLE
            noDeviceText.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }
    }

    private fun getStatus(value: Float?, min: Float, max: Float): String {
        return when {
            value == null -> "DISCONNECTED"
            value < min || value > max -> "WARNING"
            else -> "NORMAL"
        }
    }

    private fun formatTimestamp(timestamp: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT") // parse as GMT
            val date = sdf.parse(timestamp)
            val sdfLocal = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
            sdfLocal.timeZone = TimeZone.getTimeZone("GMT+08:00") // convert to GMT+8
            date?.let { sdfLocal.format(it) } ?: "--"
        } catch (e: Exception) {
            "--"
        }
    }

    private fun startPolling() { handler.post(pollingRunnable) }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(pollingRunnable)
    }
}
