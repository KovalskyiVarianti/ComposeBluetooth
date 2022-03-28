package com.example.composebluetooth.presentation

import androidx.lifecycle.ViewModel
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.BluetoothActionResult
import com.example.composebluetooth.domain.BluetoothDeviceDomainEntity
import com.example.composebluetooth.domain.BluetoothService
import com.example.composebluetooth.domain.BluetoothState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainViewModel @Inject constructor(
    bluetoothStateToMainUiStateMapper: Mapper<BluetoothState, MainUiState>,
    private val bluetoothService: BluetoothService,
    private val deviceDomainToBluetoothDevicePresentationMapper: Mapper<BluetoothDeviceDomainEntity, DevicePresentationEntity>
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(
        bluetoothStateToMainUiStateMapper.map(bluetoothService.state)
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val pairedDevices = bluetoothService.pairedDevices.map { bluetoothDevices ->
        bluetoothDevices.map { bluetoothDevice ->
            deviceDomainToBluetoothDevicePresentationMapper.map(bluetoothDevice)
        }
    }

    private val _discoveredDevicesState: MutableStateFlow<List<DevicePresentationEntity>> =
        MutableStateFlow(emptyList())

    val discoveredDevicesState = _discoveredDevicesState.asStateFlow()

    fun onBluetoothTurnedOn() {
        _uiState.value = MainUiState.BluetoothTurnedOn
    }

    fun onBluetoothTurnedOff() {
        _uiState.value = MainUiState.BluetoothTurnedOff
    }

    fun onBluetoothStateChanging(title: String) {
        _uiState.value = MainUiState.Loading(title)
    }

    fun onDeviceFound(bluetoothDevice: BluetoothDeviceDomainEntity) {
        _discoveredDevicesState.value =
            _discoveredDevicesState.value + deviceDomainToBluetoothDevicePresentationMapper.map(
                bluetoothDevice
            )
    }

    fun onDiscoveryStarted() {
        _discoveredDevicesState.value = emptyList()
    }

    fun onDiscoveryFinished() {

    }

    fun turnOnBluetooth(requestPermissionsAction: (List<String>) -> Unit) {
        val result = bluetoothService.turnOn()
        if (result is BluetoothActionResult.PermissionsRequired) {
            requestPermissionsAction(result.permissions)
        }
    }

    fun turnOffBluetooth(requestPermissionsAction: (List<String>) -> Unit) {
        val result = bluetoothService.turnOff()
        if (result is BluetoothActionResult.PermissionsRequired) {
            requestPermissionsAction(result.permissions)
        }
    }

    fun fetchPairedDevices(requestPermissionsAction: (List<String>) -> Unit) {
        val result = bluetoothService.fetchPairedDevices()
        if (result is BluetoothActionResult.PermissionsRequired) {
            requestPermissionsAction(result.permissions)
        }
    }

    fun startDiscovery(requestPermissionsAction: (List<String>) -> Unit) {
        val result = bluetoothService.startDiscovery()
        if (result is BluetoothActionResult.PermissionsRequired) {
            requestPermissionsAction(result.permissions)
        }
    }
}