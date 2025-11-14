package com.example.ed_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.adapter.HistoryAdapter
import com.example.ed_system.adapter.ReadingAdapter
import com.example.ed_system.model.HistoryAverage
import com.example.ed_system.model.Reading
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class HistoryFragment : Fragment() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyTitle: TextView

    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var readingAdapter: ReadingAdapter

    private lateinit var btn24h: Button
    private lateinit var btn7d: Button
    private lateinit var btn30d: Button


    private lateinit var barChartDO: BarChart
    private lateinit var barChartPH: BarChart
    private lateinit var barChartTemp: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Average Values
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        historyTitle = view.findViewById(R.id.historyTitle)
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(emptyList())
        historyRecyclerView.adapter = historyAdapter

        // Bar Chart

        barChartDO = view.findViewById(R.id.barChartDO)
        barChartPH = view.findViewById(R.id.barChartPH)
        barChartTemp = view.findViewById(R.id.barChartTemp)
        setupBarCharts()

        // Recent Readings
        recentRecyclerView = view.findViewById(R.id.recentReadingsRecyclerView)
        recentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        readingAdapter = ReadingAdapter(emptyList())
        recentRecyclerView.adapter = readingAdapter

        // Buttons
        btn24h = view.findViewById(R.id.btn24h)
        btn7d = view.findViewById(R.id.btn7d)
        btn30d = view.findViewById(R.id.btn30d)

        // Default data (7 Days)
        updateHistory("7 Days")
        updateRecentReadings()

        btn24h.setOnClickListener { updateHistory("24 Hours") }
        btn7d.setOnClickListener { updateHistory("7 Days") }
        btn30d.setOnClickListener { updateHistory("30 Days") }

        return view
    }


    private fun updateHistory(period: String) {
        historyTitle.text = "Average Values ($period)"

        val sampleData = when (period) {
            "24 Hours" -> listOf(
                HistoryAverage("Dissolved Oxygen", "8.9 mg/L", "Stable"),
                HistoryAverage("pH Level", "7.8", "Stable"),
                HistoryAverage("Temperature", "16.0 °C", "Stable")
            )
            "30 Days" -> listOf(
                HistoryAverage("Dissolved Oxygen", "8.3 mg/L", "Stable"),
                HistoryAverage("pH Level", "7.6", "Stable"),
                HistoryAverage("Temperature", "15.2 °C", "Stable")
            )
            else -> listOf(
                HistoryAverage("Dissolved Oxygen", "8.47 mg/L", "Stable"),
                HistoryAverage("pH Level", "7.7", "Stable"),
                HistoryAverage("Temperature", "15.0 °C", "Stable")
            )
        }

        historyAdapter.updateData(sampleData)
    }

    private fun setupBarCharts() {
        // DO Bar Chart
        val doEntries = listOf(BarEntry(0f, 8.47f), BarEntry(1f, 8.9f), BarEntry(2f, 8.3f))
        val doDataSet = BarDataSet(doEntries, "Dissolved Oxygen")
        doDataSet.color = requireContext().getColor(R.color.teal_200)
        barChartDO.data = BarData(doDataSet)
        barChartDO.description.isEnabled = false
        barChartDO.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("7d", "24h", "30d"))
        barChartDO.invalidate()

        // pH Bar Chart
        val phEntries = listOf(BarEntry(0f, 7.7f), BarEntry(1f, 7.8f), BarEntry(2f, 7.6f))
        val phDataSet = BarDataSet(phEntries, "pH Level")
        phDataSet.color = requireContext().getColor(R.color.teal_700)
        barChartPH.data = BarData(phDataSet)
        barChartPH.description.isEnabled = false
        barChartPH.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("7d", "24h", "30d"))
        barChartPH.invalidate()


        // Temperature Bar Chart
        val tempEntries = listOf(BarEntry(0f, 15.0f), BarEntry(1f, 16.0f), BarEntry(2f, 15.2f))
        val tempDataSet = BarDataSet(tempEntries, "Temperature")
        tempDataSet.color = requireContext().getColor(R.color.purple_200)
        barChartTemp.data = BarData(tempDataSet)
        barChartTemp.description.isEnabled = false
        barChartTemp.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("7d", "24h", "30d"))
        barChartTemp.invalidate()
    }


    private fun updateRecentReadings() {
        val sampleReadings = listOf(
            Reading("River Site 1", "7/4/2025 9:23:27 AM", "6.8", "10", "16.2°C"),
            Reading("River Site 3", "7/4/2025 9:23:27 AM", "8.9", "8.0", "13.1°C"),
            Reading("River Site 2", "7/4/2025 9:23:27 AM", "8.5", "7.1", "15.8°C")
        )

        readingAdapter.updateData(sampleReadings)
    }
}
