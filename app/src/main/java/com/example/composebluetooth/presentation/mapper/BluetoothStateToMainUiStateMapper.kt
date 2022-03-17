package com.example.composebluetooth.presentation.mapper

import android.content.Context
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.R
import com.example.composebluetooth.domain.BluetoothState
import com.example.composebluetooth.presentation.MainUiState
import javax.inject.Inject

class BluetoothStateToMainUiStateMapper @Inject constructor(private val context: Context) :
    Mapper<BluetoothState, MainUiState> {
    override fun map(from: BluetoothState): MainUiState {
        return when (from) {
            BluetoothState.TurnedOff -> MainUiState.BluetoothTurnedOff
            BluetoothState.TurnedOn -> MainUiState.BluetoothTurnedOn
            BluetoothState.TurningOff -> MainUiState.Loading(context.getString(R.string.bluetooth_turning_off))
            BluetoothState.TurningOn -> MainUiState.Loading(context.getString(R.string.bluetooth_turning_on))
            BluetoothState.Unknown -> MainUiState.Loading(context.getString(R.string.loading))
        }
    }
}