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

    private val _devicesState: MutableStateFlow<DeviceListState> =
        MutableStateFlow(DeviceListState.Loading)
    val devicesState: StateFlow<DeviceListState> = _devicesState.asStateFlow()

    fun onBluetoothTurnedOn() {
        _uiState.value = MainUiState.BluetoothTurnedOn
    }

    fun onBluetoothTurnedOff() {
        _uiState.value = MainUiState.BluetoothTurnedOff
    }

    fun onBluetoothStateChanging(title: String) {
        _uiState.value = MainUiState.Loading(title)
    }

    fun turnOnBluetooth(requestPermissionAction: (String) -> Unit) {
        val result = bluetoothService.turnOn()
        if (result is BluetoothActionResult.PermissionRequired) {
            requestPermissionAction(result.permission)
        }
    }

    fun turnOffBluetooth(requestPermissionAction: (String) -> Unit) {
        val result = bluetoothService.turnOff()
        if (result is BluetoothActionResult.PermissionRequired) {
            requestPermissionAction(result.permission)
        }
    }

    fun loadDevices(requestPermissionAction: (String) -> Unit) {
        _devicesState.value = DeviceListState.Result(
            bluetoothService.getPairedDevices { requestPermissionAction(it) }.map {
                deviceDomainToBluetoothDevicePresentationMapper.map(it)
            }
        )
    }
}