package com.example.ed_system


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.model.Monitor
import com.example.ed_system.adapter.MonitorAdapter

class MonitorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MonitorAdapter
    private val monitorList = mutableListOf<Monitor>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_monitor, container, false)

        recyclerView = view.findViewById(R.id.monitorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Example: multiple sites/devices
        monitorList.add(
            Monitor(
                siteName = "Coastal Site 7",
                siteTag = "COASTAL",
                deviceId = "EFD-757",
                lastUpdated = "9:03:07 AM",
                doValue = "7.44 mg/L", doStatus = "NORMAL",
                phValue = "8.4", phStatus = "WARNING",
                tempValue = "16.70 °C", tempStatus = "NORMAL"
            )
        )

        monitorList.add(
            Monitor(
                siteName = "Lake Site 1",
                siteTag = "LAKE",
                deviceId = "LK-204",
                lastUpdated = "8:55:45 AM",
                doValue = "6.21 mg/L", doStatus = "NORMAL",
                phValue = "7.2", phStatus = "NORMAL",
                tempValue = "19.3 °C", tempStatus = "DANGER"
            )
        )

        adapter = MonitorAdapter(monitorList)
        recyclerView.adapter = adapter

        return view
    }
}
