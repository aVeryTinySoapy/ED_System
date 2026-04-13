package com.example.ed_system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import adapter.AlertAdapter
import com.example.ed_system.adapter.DeviceAdapter
import com.example.ed_system.DeviceManager
import com.example.ed_system.model.Alert
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var deviceSiteLocation: TextView
    private lateinit var doIcon: ImageView
    private lateinit var doName: TextView
    private lateinit var doValue: TextView
    private lateinit var doStatus: TextView
    private lateinit var phIcon: ImageView
    private lateinit var phName: TextView
    private lateinit var phValue: TextView
    private lateinit var phStatus: TextView
    private lateinit var tempIcon: ImageView
    private lateinit var tempName: TextView
    private lateinit var tempValue: TextView
    private lateinit var tempStatus: TextView
    private lateinit var updatedTemp: TextView
    private lateinit var updatedDO: TextView
    private lateinit var updatedPH: TextView
    private lateinit var alertsRecyclerView: RecyclerView
    private lateinit var alertsAdapter: AlertAdapter
    private lateinit var noAlertsText: TextView
    private lateinit var deviceRecycler: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var noDevicePlaceholder: TextView

    private lateinit var tempCard: View
    private lateinit var phCard: View
    private lateinit var doCard: View

    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())
    private val pollingInterval = 10000L

    // Track active alerts
    private val alertStatusMap = mutableMapOf<String, Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        deviceSiteLocation = view.findViewById(R.id.deviceSiteLocation)
        noDevicePlaceholder = view.findViewById(R.id.noDevicePlaceholder)

        tempCard = view.findViewById(R.id.tempCard)
        phCard = view.findViewById(R.id.phCard)
        doCard = view.findViewById(R.id.doCard)

        view.findViewById<View>(R.id.doCard).apply {
            doIcon = findViewById(R.id.icon)
            doName = findViewById(R.id.name)
            doValue = findViewById(R.id.value)
            doStatus = findViewById(R.id.status)
            updatedDO = findViewById(R.id.updated)
        }

        view.findViewById<View>(R.id.phCard).apply {
            phIcon = findViewById(R.id.icon)
            phName = findViewById(R.id.name)
            phValue = findViewById(R.id.value)
            phStatus = findViewById(R.id.status)
            updatedPH = findViewById(R.id.updated)
        }

        view.findViewById<View>(R.id.tempCard).apply {
            tempIcon = findViewById(R.id.icon)
            tempName = findViewById(R.id.name)
            tempValue = findViewById(R.id.value)
            tempStatus = findViewById(R.id.status)
            updatedTemp = findViewById(R.id.updated)
        }

        // Assign static icons and names
        tempName.text = "Temperature"
        tempIcon.setImageResource(R.drawable.ic_temperature)
        doName.text = "Dissolved Oxygen"
        doIcon.setImageResource(R.drawable.ic_oxygen)
        phName.text = "pH Level"
        phIcon.setImageResource(R.drawable.ic_ph)

        // Alerts RecyclerView
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView)
        noAlertsText = view.findViewById(R.id.noAlertsText)
        alertsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        alertsAdapter = AlertAdapter()
        alertsRecyclerView.adapter = alertsAdapter

        // Devices RecyclerView
        deviceRecycler = view.findViewById(R.id.deviceRecycler)
        deviceRecycler.layoutManager = LinearLayoutManager(requireContext())
        deviceAdapter = DeviceAdapter(requireContext())
        deviceRecycler.adapter = deviceAdapter

        // Submit list and update UI for empty device list
        deviceAdapter.submitList(DeviceManager.deviceList)
        updateDeviceSection()

        startPolling()
        return view
    }

    private fun updateDeviceSection() {
        if (DeviceManager.deviceList.isEmpty()) {
            tempCard.visibility = View.GONE
            phCard.visibility = View.GONE
            doCard.visibility = View.GONE
            alertsRecyclerView.visibility = View.GONE
            noAlertsText.visibility = View.GONE
            deviceRecycler.visibility = View.GONE
            noDevicePlaceholder.visibility = View.VISIBLE
        } else {
            tempCard.visibility = View.VISIBLE
            phCard.visibility = View.VISIBLE
            doCard.visibility = View.VISIBLE
            alertsRecyclerView.visibility = View.VISIBLE
            noAlertsText.visibility = View.VISIBLE
            deviceRecycler.visibility = View.VISIBLE
            noDevicePlaceholder.visibility = View.GONE
        }
    }

    private val pollingRunnable = object : Runnable {
        override fun run() {
            val targetDevice = DeviceManager.selectedDevice ?: DeviceManager.deviceList.firstOrNull()
            targetDevice?.let { fetchDeviceData(it) }
            handler.postDelayed(this, pollingInterval)
        }
    }

    private fun startPolling() { handler.post(pollingRunnable) }

    private fun fetchDeviceData(device: com.example.ed_system.model.Device) {
        val url = "https://api.thingspeak.com/channels/${device.channelId}/feeds.json?results=1"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { setDeviceOffline(device) }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body.isNullOrEmpty()) return setDeviceOffline(device)
                try {
                    val feeds = JSONObject(body).optJSONArray("feeds")
                    if (feeds != null && feeds.length() > 0) {
                        val feed = feeds.getJSONObject(0)
                        device.isOnline = true
                        device.lastReading =
                            "Temp: ${feed.optString("field1", "--")} | DO: ${feed.optString("field2", "--")} | pH: ${feed.optString("field3", "--")}"

                        if (DeviceManager.selectedDevice == null) DeviceManager.selectedDevice = device

                        activity?.runOnUiThread {
                            deviceAdapter.notifyDataSetChanged()
                            updateParameterCards(feed)
                        }
                    } else setDeviceOffline(device)
                } catch (e: Exception) { setDeviceOffline(device) }
            }
        })
    }

    private fun setDeviceOffline(device: com.example.ed_system.model.Device) {
        device.isOnline = false
        device.lastReading = "--"
        activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
    }

    private fun updateParameterCards(feed: JSONObject) {
        val temp = feed.optString("field1", "--")
        val doValueStr = feed.optString("field2", "--")
        val ph = feed.optString("field3", "--")
        val createdAt = feed.optString("created_at", null)
        val timestamp = if (createdAt != null) {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            sdfInput.timeZone = TimeZone.getTimeZone("GMT")
            val date = sdfInput.parse(createdAt)
            val sdfOutput = SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault())
            sdfOutput.timeZone = TimeZone.getDefault()
            date?.let { sdfOutput.format(it) } ?: "Updated just now"
        } else "Updated just now"

        tempValue.text = temp
        doValue.text = doValueStr
        phValue.text = ph
        updatedTemp.text = timestamp
        updatedDO.text = timestamp
        updatedPH.text = timestamp

        val alerts = mutableListOf<Alert>()

        // Temperature status
        if (temp != "--") {
            val tempFloat = temp.toFloatOrNull() ?: 0f
            val tempStatusStr = when {
                tempFloat < 10 || tempFloat > 30 -> "WARNING"
                else -> "NORMAL"
            }
            setStatus(tempStatus, tempStatusStr)
            if (tempStatusStr != "NORMAL") {
                alerts.add(
                    Alert(
                        iconRes = R.drawable.ic_warning,
                        title = "Temperature Alert",
                        description = "Temperature is $temp °C",
                        value = "Temp: $temp °C",
                        threshold = "Safe: 10-30 °C",
                        status = tempStatusStr,
                        timeAgo = timestamp
                    )
                )
            }
        } else setStatus(tempStatus, "DISCONNECTED")

        // Dissolved Oxygen status
        if (doValueStr != "--") {
            val doFloat = doValueStr.toFloatOrNull() ?: 0f
            val doStatusStr = when {
                doFloat < 7 || doFloat > 40 -> "WARNING"
                else -> "NORMAL"
            }
            setStatus(doStatus, doStatusStr)
            if (doStatusStr != "NORMAL") {
                alerts.add(
                    Alert(
                        iconRes = R.drawable.ic_warning,
                        title = "DO Alert",
                        description = "Dissolved Oxygen is $doValueStr mg/L",
                        value = "DO: $doValueStr mg/L",
                        threshold = "Safe: 5-12 mg/L",
                        status = doStatusStr,
                        timeAgo = timestamp
                    )
                )
            }
        } else setStatus(doStatus, "DISCONNECTED")

        // pH status
        if (ph != "--") {
            val phFloat = ph.toFloatOrNull() ?: 0f
            val phStatusStr = when {
                phFloat < 6.5 || phFloat > 8.5 -> "WARNING"
                else -> "NORMAL"
            }
            setStatus(phStatus, phStatusStr)
            if (phStatusStr != "NORMAL") {
                alerts.add(
                    Alert(
                        iconRes = R.drawable.ic_warning,
                        title = "pH Alert",
                        description = "pH Level is $ph",
                        value = "pH: $ph",
                        threshold = "Safe: 6.5-8.5",
                        status = phStatusStr,
                        timeAgo = timestamp
                    )
                )
            }
        } else setStatus(phStatus, "DISCONNECTED")

        showAlerts(alerts)
    }

    private fun setStatus(statusTextView: TextView, status: String) {
        statusTextView.text = status
        val color = when (status.uppercase()) {
            "NORMAL" -> android.R.color.holo_green_dark
            "WARNING" -> android.R.color.holo_orange_dark
            "DANGER" -> android.R.color.holo_red_dark
            "DISCONNECTED" -> android.R.color.darker_gray
            else -> android.R.color.darker_gray
        }
        statusTextView.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun showAlerts(alerts: List<Alert>) {
        if (alerts.isEmpty()) {
            alertsRecyclerView.visibility = View.GONE
            noAlertsText.visibility = View.VISIBLE
            alertStatusMap.clear()
        } else {
            alertsRecyclerView.visibility = View.VISIBLE
            noAlertsText.visibility = View.GONE
            alertsAdapter.submitList(alerts)

            alerts.forEach { alert ->
                val alertId = "${alert.title}_${alert.value}"

                // New alert
                if (alertStatusMap[alertId] != true) {
                    showNotification(listOf(alert))
                    alertStatusMap[alertId] = true
                }
            }

            // Check for resolved alerts
            val activeIds = alerts.map { "${it.title}_${it.value}" }
            val resolved = alertStatusMap.keys - activeIds
            resolved.forEach { id ->
                showResolvedNotification(id)
                alertStatusMap.remove(id)
            }
        }
    }

    private fun showNotification(alerts: List<Alert>) {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alert_channel",
                "Device Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notifications for device alerts" }
            notificationManager.createNotificationChannel(channel)
        }

        val alertTitles = alerts.joinToString("\n") { "${it.title}: ${it.value}" }

        val notification = NotificationCompat.Builder(requireContext(), "alert_channel")
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("New Alert(s)")
            .setContentText(alerts.first().description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(alertTitles))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showResolvedNotification(alertId: String) {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(requireContext(), "alert_channel")
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("Resolved Alert")
            .setContentText("$alertId is back to normal.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(pollingRunnable)
    }
}
