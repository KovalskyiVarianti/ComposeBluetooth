package com.example.composebluetooth.presentation

sealed interface MainUiState {
    object BluetoothTurnedOn : MainUiState
    object BluetoothTurnedOff : MainUiState
    data class Loading(val title: String) : MainUiState
}