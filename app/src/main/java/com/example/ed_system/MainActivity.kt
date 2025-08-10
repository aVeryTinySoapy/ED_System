package com.example.ed_system

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ed_system.adapter.DeviceAdapter
import com.example.ed_system.model.Device
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private val deviceList = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_monitor -> true
                R.id.nav_history -> true
                R.id.nav_settings -> true
                else -> false
            }
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.deviceRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        deviceAdapter = DeviceAdapter(deviceList, this)
        recyclerView.adapter = deviceAdapter

        // Sample data
        deviceList.add(
            Device("Lake Monitor A", "EFD-001", true, 8, 92, "3m ago")
        )
        deviceList.add(
            Device("Lake Monitor B", "EFD-002", false, 72, 68, "10m ago")
        )

        deviceAdapter.notifyDataSetChanged()
    }
}
