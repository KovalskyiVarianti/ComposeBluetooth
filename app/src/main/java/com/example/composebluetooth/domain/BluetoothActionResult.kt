package com.example.composebluetooth.domain

sealed interface BluetoothActionResult {
    object Success : BluetoothActionResult
    data class PermissionRequired(val permission: String) : BluetoothActionResult
}