package com.example.ed_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.adapter.DeviceAdapter
import com.example.ed_system.model.Device

class SettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private val deviceList = mutableListOf<Device>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        recyclerView = view.findViewById(R.id.deviceRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        deviceAdapter = DeviceAdapter(deviceList, requireContext())
        recyclerView.adapter = deviceAdapter

        // Sample devices
        deviceList.add(Device("Lake Monitor A", "EFD-001", true, 8, 92, "3m ago"))
        deviceList.add(Device("Lake Monitor B", "EFD-002", false, 72, 68, "10m ago"))

        deviceAdapter.notifyDataSetChanged()

        return view
    }
}
