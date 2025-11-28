package com.example.ed_system

import model.Reading

object DataRepository {

    // Store readings per device using the device's channelId
    private val readingsMap = mutableMapOf<String, MutableList<Reading>>()

    /** Add a new reading for a specific device */
    fun addReading(deviceId: String, reading: Reading) {
        if (!readingsMap.containsKey(deviceId)) {
            readingsMap[deviceId] = mutableListOf()
        }
        readingsMap[deviceId]?.add(reading)
    }

    /** Get all readings for a specific device */
    fun getReadings(deviceId: String): List<Reading> {
        return readingsMap[deviceId]?.toList() ?: emptyList()
    }

    /** Clear readings for a specific device (optional) */
    fun clearReadings(deviceId: String) {
        readingsMap[deviceId]?.clear()
    }

    /** Clear all readings for all devices (optional) */
    fun clearAll() {
        readingsMap.clear()
    }
}
