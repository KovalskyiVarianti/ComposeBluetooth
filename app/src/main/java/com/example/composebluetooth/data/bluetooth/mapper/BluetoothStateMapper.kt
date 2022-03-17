package com.example.composebluetooth.data.bluetooth.mapper

import android.bluetooth.BluetoothAdapter
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.BluetoothState
import javax.inject.Inject

class BluetoothStateMapper @Inject constructor() : Mapper<Int, BluetoothState> {
    override fun map(from: Int): BluetoothState {
        return when (from) {
            BluetoothAdapter.STATE_ON -> BluetoothState.TurnedOn
            BluetoothAdapter.STATE_OFF -> BluetoothState.TurnedOff
            BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TurningOn
            BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TurningOff
            else -> BluetoothState.Unknown
        }
    }
}