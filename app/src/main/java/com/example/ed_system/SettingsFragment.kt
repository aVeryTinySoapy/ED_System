package com.example.ed_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.model.Device
import com.example.ed_system.adapter.DeviceAdapter

class SettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var placeholder: View
    private val deviceList = mutableListOf<Device>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        recyclerView = view.findViewById(R.id.deviceRecyclerView)
        placeholder = view.findViewById(R.id.devicePlaceholder)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        deviceAdapter = DeviceAdapter(requireContext())
        recyclerView.adapter = deviceAdapter

        // Sample data (uncomment to test)
        deviceList.add(Device("Lake Monitor A", "EFD-001", true, 8, 92, "3m ago"))
        deviceList.add(Device("Lake Monitor B", "EFD-002", false, 72, 68, "10m ago"))

        updateDeviceSection()

        return view
    }

    private fun updateDeviceSection() {
        if (deviceList.isEmpty()) {
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
            deviceAdapter.submitList(deviceList)
        }
    }
}