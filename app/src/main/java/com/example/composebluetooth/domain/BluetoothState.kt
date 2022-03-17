package com.example.composebluetooth.domain

sealed interface BluetoothState{
    object TurnedOn : BluetoothState
    object TurningOn : BluetoothState
    object TurnedOff : BluetoothState
    object TurningOff : BluetoothState
    object Unknown : BluetoothState
}