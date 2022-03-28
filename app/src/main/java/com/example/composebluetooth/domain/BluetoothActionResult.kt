package com.example.composebluetooth.domain

sealed interface BluetoothActionResult {
    object Success : BluetoothActionResult
    object Failure : BluetoothActionResult
    data class PermissionsRequired(val permissions: List<String>) : BluetoothActionResult
}