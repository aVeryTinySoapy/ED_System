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
import com.example.ed_system.adapter.RecentReadingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import model.RecentReading
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread
import android.os.Environment
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var lineChartTemp: LineChart
    private lateinit var lineChartDO: LineChart
    private lateinit var lineChartPH: LineChart
    private lateinit var recentRecycler: RecyclerView
    private lateinit var placeholder: TextView
    private lateinit var btnExport: MaterialButton

    private val handler = Handler(Looper.getMainLooper())
    private val recentList = mutableListOf<RecentReading>()
    private lateinit var adapter: RecentReadingAdapter

    private val pollingInterval = 10_000L // 10 seconds

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        lineChartTemp = view.findViewById(R.id.barChartTemp)
        lineChartDO = view.findViewById(R.id.barChartDO)
        lineChartPH = view.findViewById(R.id.barChartPH)
        recentRecycler = view.findViewById(R.id.recentReadingsRecyclerView)
        placeholder = view.findViewById(R.id.tvExportHint)
        btnExport = view.findViewById(R.id.btnExportData)

        val deviceName = DeviceManager.selectedDevice?.name ?: "Unknown Device"
        adapter = RecentReadingAdapter(recentList, deviceName)
        recentRecycler.layoutManager = LinearLayoutManager(requireContext())
        recentRecycler.adapter = adapter

        btnExport.setOnClickListener { exportCsv() }

        startPolling()
        return view
    }

    private fun startPolling() {
        handler.post(object : Runnable {
            override fun run() {
                fetchHistoryData()
                handler.postDelayed(this, pollingInterval)
            }
        })
    }

    private fun fetchHistoryData() {
        val device = DeviceManager.selectedDevice
        if (device == null) {
            handler.post {
                placeholder.visibility = View.VISIBLE
                placeholder.text = "No device added yet. Please add a device in Settings."
                lineChartTemp.visibility = View.GONE
                lineChartDO.visibility = View.GONE
                lineChartPH.visibility = View.GONE
                recentRecycler.visibility = View.GONE
            }
            return
        }

        handler.post {
            placeholder.visibility = View.GONE
            lineChartTemp.visibility = View.VISIBLE
            lineChartDO.visibility = View.VISIBLE
            lineChartPH.visibility = View.VISIBLE
            recentRecycler.visibility = View.VISIBLE
        }

        thread {
            try {
                val url = "https://api.thingspeak.com/channels/${device.channelId}/feeds.json?results=25"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val feeds = json.optJSONArray("feeds") ?: return@thread

                recentList.clear()
                val allTemp = mutableListOf<Float>()
                val allDo = mutableListOf<Float>()
                val allPh = mutableListOf<Float>()
                val allTimestamps = mutableListOf<String>()

                for (i in 0 until feeds.length()) {
                    val f = feeds.getJSONObject(i)
                    val temp = f.optString("field1", "0").toFloatOrNull() ?: 0f
                    val doVal = f.optString("field2", "0").toFloatOrNull() ?: 0f
                    val phVal = f.optString("field3", "0").toFloatOrNull() ?: 0f
                    val createdAt = f.optString("created_at", "--")
                    val formattedTimestamp = formatTimestamp(createdAt)

                    allTemp.add(temp)
                    allDo.add(doVal)
                    allPh.add(phVal)
                    allTimestamps.add(formattedTimestamp)

                    recentList.add(RecentReading(formattedTimestamp, doVal, phVal, temp))
                }

                val count = allTemp.size
                val fromIndex = if (count > 5) count - 5 else 0
                val tempLast5 = allTemp.subList(fromIndex, count)
                val doLast5 = allDo.subList(fromIndex, count)
                val phLast5 = allPh.subList(fromIndex, count)
                val timestampsLast5 = allTimestamps.subList(fromIndex, count)

                handler.post {

                    setupSingleLineChart(lineChartTemp, tempLast5, timestampsLast5, "Temperature (°C)", 0xFFFF5722.toInt())
                    setupSingleLineChart(lineChartDO, doLast5, timestampsLast5, "Dissolved Oxygen (mg/L)", 0xFF4CAF50.toInt())
                    setupSingleLineChart(lineChartPH, phLast5, timestampsLast5, "pH Level", 0xFF2196F3.toInt())

                    recentList.reverse()
                    adapter.notifyDataSetChanged()
                    recentRecycler.scrollToPosition(0)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun setupSingleLineChart(chart: LineChart, values: List<Float>, timestamps: List<String>, title: String, lineColor: Int) {

        val entries = values.mapIndexed { index, v ->
            Entry(index.toFloat(), v)
        }

        val dataSet = LineDataSet(entries, title).apply {
            color = lineColor
            setCircleColor(lineColor)
            lineWidth = 2.5f
            circleRadius = 4f
            valueTextColor = 0xFF000000.toInt()
            valueTextSize = 11f
            setDrawValues(true)
        }

        chart.data = LineData(dataSet)

        chart.description.apply {
            text = title
            textSize = 14f
            textColor = 0xFF000000.toInt()
        }

        chart.axisLeft.apply {
            textColor = 0xFF000000.toInt()
            axisMinimum = 0f
        }

        chart.axisRight.isEnabled = false

        chart.legend.textColor = 0xFF000000.toInt()

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            textColor = 0xFF000000.toInt()
            valueFormatter = IndexAxisValueFormatter(timestamps)
            labelRotationAngle = -45f
        }

        chart.invalidate()
    }

    private fun exportCsv() {
        if (recentList.isEmpty()) {
            Toast.makeText(requireContext(), "No data to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val deviceName = DeviceManager.selectedDevice?.name ?: "UnknownDevice"
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val fileName = "${deviceName}_${sdf.format(Date())}.csv"

        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, fileName)

        try {
            FileWriter(file).use { writer ->
                writer.append("Device Name,Timestamp,DO (mg/L),pH,Temperature (°C)\n")
                recentList.forEach { r ->
                    writer.append("$deviceName,${r.timestamp},${r.doValue},${r.phValue},${r.tempValue}\n")
                }
            }

            Toast.makeText(requireContext(), "CSV exported to Downloads.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun formatTimestamp(timestamp: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            val date = sdf.parse(timestamp)
            val sdfLocal = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault())
            sdfLocal.timeZone = TimeZone.getTimeZone("GMT+08:00")
            date?.let { sdfLocal.format(it) } ?: "--"
        } catch (e: Exception) {
            "--"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}