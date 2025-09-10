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
import com.example.ed_system.model.HistoryAverage

class HistoryFragment : Fragment() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyTitle: TextView

    private lateinit var btn24h: Button
    private lateinit var btn7d: Button
    private lateinit var btn30d: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        historyTitle = view.findViewById(R.id.historyTitle)

        btn24h = view.findViewById(R.id.btn24h)
        btn7d = view.findViewById(R.id.btn7d)
        btn30d = view.findViewById(R.id.btn30d)

        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(emptyList())
        historyRecyclerView.adapter = historyAdapter

        // Default data (7 Days)
        updateHistory("7 Days")

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
}
