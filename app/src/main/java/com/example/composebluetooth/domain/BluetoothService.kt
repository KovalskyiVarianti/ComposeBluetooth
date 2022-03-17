package com.example.composebluetooth.domain

interface BluetoothService {
    val state: BluetoothState
    fun turnOn() : BluetoothActionResult
    fun turnOff() : BluetoothActionResult
    fun getPairedDevices(onPermissionDenied: (permission: String) -> Unit): List<BluetoothDeviceDomainEntity>
}