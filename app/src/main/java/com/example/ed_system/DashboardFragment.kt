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
import com.example.ed_system.model.Alert
import com.example.ed_system.adapter.AlertAdapter

class DashboardFragment : Fragment() {

    private lateinit var deviceSiteLocation: TextView

    // Parameter cards
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

    // Active Alerts
    private lateinit var alertsRecyclerView: RecyclerView
    private lateinit var alertsAdapter: AlertAdapter
    private lateinit var noAlertsText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Device location
        deviceSiteLocation = view.findViewById(R.id.deviceSiteLocation)

        // Dissolved Oxygen Card
        view.findViewById<View>(R.id.doCard).apply {
            doIcon = findViewById(R.id.icon)
            doName = findViewById(R.id.name)
            doValue = findViewById(R.id.value)
            doStatus = findViewById(R.id.status)
        }

        // pH Card
        view.findViewById<View>(R.id.phCard).apply {
            phIcon = findViewById(R.id.icon)
            phName = findViewById(R.id.name)
            phValue = findViewById(R.id.value)
            phStatus = findViewById(R.id.status)
        }

        // Temperature Card
        view.findViewById<View>(R.id.tempCard).apply {
            tempIcon = findViewById(R.id.icon)
            tempName = findViewById(R.id.name)
            tempValue = findViewById(R.id.value)
            tempStatus = findViewById(R.id.status)
        }

        // Alerts section
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView)
        noAlertsText = view.findViewById(R.id.noAlertsText)

        alertsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        alertsAdapter = AlertAdapter()
        alertsRecyclerView.adapter = alertsAdapter

        // Dashboard sample data
        updateDashboard(
            siteLocation = "Lake Site 1 • LAKE",
            dissolvedOxygen = "7.44 mg/L",
            doStatusText = "NORMAL",
            phLevel = "8.4",
            phStatusText = "WARNING",
            temperature = "16.70 °C",
            tempStatusText = "NORMAL"
        )

        // Sample alerts
        val sampleAlerts = listOf(
            Alert(
                iconRes = R.drawable.ic_warning,
                title = "High pH Level",
                description = "pH is above safe range (8.4)",
                value = "pH: 8.4",
                threshold = "Threshold: 7.5",
                status = "WARNING",
                timeAgo = "16m ago"
            )
        )
        //val sampleAlerts = emptyList<Alert>()

        showAlerts(sampleAlerts)

        return view
    }

    private fun updateDashboard(
        siteLocation: String,
        dissolvedOxygen: String,
        doStatusText: String,
        phLevel: String,
        phStatusText: String,
        temperature: String,
        tempStatusText: String
    ) {
        deviceSiteLocation.text = siteLocation

        // DO card
        doIcon.setImageResource(R.drawable.ic_oxygen)
        doName.text = "Dissolved Oxygen"
        doValue.text = dissolvedOxygen
        setStatus(doStatus, doStatusText)

        // pH card
        phIcon.setImageResource(R.drawable.ic_ph)
        phName.text = "pH Level"
        phValue.text = phLevel
        setStatus(phStatus, phStatusText)

        // Temp card
        tempIcon.setImageResource(R.drawable.ic_temperature)
        tempName.text = "Temperature"
        tempValue.text = temperature
        setStatus(tempStatus, tempStatusText)
    }

    private fun setStatus(statusTextView: TextView, status: String) {
        statusTextView.text = status
        val color = when (status.uppercase()) {
            "NORMAL" -> android.R.color.holo_green_dark
            "WARNING" -> android.R.color.holo_orange_dark
            "DANGER" -> android.R.color.holo_red_dark
            else -> android.R.color.darker_gray
        }
        statusTextView.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun showAlerts(alerts: List<Alert>) {
        if (alerts.isEmpty()) {
            alertsRecyclerView.visibility = View.GONE
            noAlertsText.visibility = View.VISIBLE
        } else {
            alertsRecyclerView.visibility = View.VISIBLE
            noAlertsText.visibility = View.GONE
            alertsAdapter.submitList(alerts)
        }
    }
}
