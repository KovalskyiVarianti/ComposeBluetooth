package com.example.composebluetooth.presentation

sealed interface DeviceListState {
    data class Result(val data: List<DevicePresentationEntity>) : DeviceListState
    data class Error(val e: Throwable) : DeviceListState
    object Loading : DeviceListState
}