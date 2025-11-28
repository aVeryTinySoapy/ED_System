package com.example.ed_system

import com.example.ed_system.model.Device

object DeviceManager {
    val deviceList = mutableListOf<Device>()
    var selectedDevice: Device? = null
}
