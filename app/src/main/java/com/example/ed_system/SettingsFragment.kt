package com.example.ed_system

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.adapter.DeviceAdapter
import com.example.ed_system.model.Device
import com.example.ed_system.DeviceManager
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var placeholder: View
    private lateinit var btnAddDevice: Button
    private lateinit var cardDataExport: View
    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())
    private val pollingInterval = 10000L // 10s

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        recyclerView = view.findViewById(R.id.deviceRecyclerView)
        placeholder = view.findViewById(R.id.devicePlaceholder)
        btnAddDevice = view.findViewById(R.id.btnAddDevice)
        cardDataExport = view.findViewById(R.id.cardDataExport)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        deviceAdapter = DeviceAdapter(requireContext())
        recyclerView.adapter = deviceAdapter

        deviceAdapter.submitList(DeviceManager.deviceList)
        updateDeviceSection()

        btnAddDevice.setOnClickListener { showAddDeviceDialog() }
        cardDataExport.setOnClickListener { exportAllDevicesCsv() }

        startPolling()
        return view
    }

    private fun showAddDeviceDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val nameInput = EditText(requireContext())
        nameInput.hint = "Device Name"

        val channelInput = EditText(requireContext())
        channelInput.hint = "ThingSpeak Channel ID"

        layout.addView(nameInput)
        layout.addView(channelInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Device")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val channelId = channelInput.text.toString().trim()

                if (name.isEmpty() || channelId.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Prevent duplicate channel IDs
                val duplicate = DeviceManager.deviceList.any { it.channelId == channelId }
                if (duplicate) {
                    Toast.makeText(requireContext(), "Device with this Channel ID already exists.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newDevice = Device(
                    name = name,
                    deviceId = "TS-$channelId",
                    channelId = channelId,
                    isOnline = false,
                    lastReading = "--"
                )

                DeviceManager.deviceList.add(newDevice)
                if (DeviceManager.selectedDevice == null) {
                    DeviceManager.selectedDevice = newDevice
                }

                deviceAdapter.submitList(DeviceManager.deviceList)
                updateDeviceSection()
                fetchDeviceData(newDevice)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateDeviceSection() {
        if (DeviceManager.deviceList.isEmpty()) {
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
        }
    }

    private fun fetchDeviceData(device: Device) {
        val url = "https://api.thingspeak.com/channels/${device.channelId}/feeds.json?results=1"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                device.isOnline = false
                device.lastReading = "--"
                activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                try {
                    val json = JSONObject(body)
                    val feeds = json.optJSONArray("feeds")
                    if (feeds != null && feeds.length() > 0) {
                        val feed = feeds.getJSONObject(0)
                        device.isOnline = true
                        device.lastReading =
                            "Temp: ${feed.optString("field1", "--")} | pH: ${feed.optString("field2", "--")} | DO: ${feed.optString("field3", "--")}"
                        activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
                    } else {
                        device.isOnline = false
                        device.lastReading = "--"
                        activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
                    }
                } catch (e: Exception) {
                    device.isOnline = false
                    device.lastReading = "--"
                    activity?.runOnUiThread { deviceAdapter.notifyDataSetChanged() }
                }
            }
        })
    }

    private val pollingRunnable = object : Runnable {
        override fun run() {
            DeviceManager.deviceList.forEach { fetchDeviceData(it) }
            handler.postDelayed(this, pollingInterval)
        }
    }

    private fun startPolling() { handler.post(pollingRunnable) }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(pollingRunnable)
    }

    // --- CSV Export Functionality for all devices (full history and report) ---
    private fun exportAllDevicesCsv() {
        if (DeviceManager.deviceList.isEmpty()) {
            Toast.makeText(requireContext(), "No device data to export.", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
                val fileName = "FullDeviceReport_${sdf.format(Date())}.csv"

                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloads, fileName)
                val writer = FileWriter(file)

                writer.append("FULL DEVICE HISTORY AND REPORT\n")
                writer.append("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n\n")

                DeviceManager.deviceList.forEach { device ->
                    writer.append("====================================================\n")
                    writer.append("Device: ${device.name} (Channel ID: ${device.channelId})\n")
                    writer.append("----------------------------------------------------\n")
                    writer.append("Timestamp,DO (mg/L),pH,Temperature (°C)\n")

                    val url = "https://api.thingspeak.com/channels/${device.channelId}/feeds.json?results=1000"
                    val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
                    val body = response.body?.string() ?: ""
                    val feeds = JSONObject(body).optJSONArray("feeds") ?: return@forEach

                    var totalTemp = 0.0
                    var totalDO = 0.0
                    var totalPH = 0.0
                    var count = 0

                    for (i in 0 until feeds.length()) {
                        val f = feeds.getJSONObject(i)
                        val temp = f.optString("field1", "0").toDoubleOrNull() ?: 0.0
                        val doVal = f.optString("field2", "0").toDoubleOrNull() ?: 0.0
                        val phVal = f.optString("field3", "0").toDoubleOrNull() ?: 0.0
                        val createdAt = f.optString("created_at", "--")

                        writer.append("$createdAt,$doVal,$phVal,$temp\n")

                        totalTemp += temp
                        totalDO += doVal
                        totalPH += phVal
                        count++
                    }

                    if (count > 0) {
                        writer.append("----------------------------------------------------\n")
                        writer.append("Summary for ${device.name}:\n")
                        writer.append("Average DO,Average pH,Average Temp\n")
                        writer.append("${String.format("%.2f", totalDO / count)},${String.format("%.2f", totalPH / count)},${String.format("%.2f", totalTemp / count)}\n\n")
                    }
                    writer.append("\n")
                }

                writer.flush()
                writer.close()

                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    file
                )

                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                activity?.runOnUiThread {
                    startActivity(android.content.Intent.createChooser(intent, "Share CSV"))
                    Toast.makeText(requireContext(), "Full historical CSV exported.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
