package com.example.composebluetooth.domain

import kotlinx.coroutines.flow.StateFlow

interface BluetoothService {
    val state: BluetoothState
    val pairedDevices : StateFlow<List<BluetoothDeviceDomainEntity>>
    fun turnOn() : BluetoothActionResult
    fun turnOff() : BluetoothActionResult
    fun findPairedDevices(onPermissionDenied: (permission: String) -> Unit)
    fun discoverDevices()
}