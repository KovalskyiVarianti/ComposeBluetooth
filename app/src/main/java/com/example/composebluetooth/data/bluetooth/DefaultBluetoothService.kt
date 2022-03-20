package com.example.composebluetooth.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DefaultBluetoothService @Inject constructor(
    private val context: Context,
    private val permissionVerifier: PermissionVerifier,
    private val bluetoothStateMapper: Mapper<Int, BluetoothState>,
) : BluetoothService {

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    override val pairedDevices: MutableStateFlow<List<BluetoothDeviceDomainEntity>> =
        MutableStateFlow(emptyList())

    override val state: BluetoothState
        get() = bluetoothStateMapper.map(bluetoothManager.adapter.state)

    override fun turnOn(): BluetoothActionResult {
        val adapter = bluetoothManager.adapter
        return checkBluetoothPermission(
            onGranted = {
                adapter.enable()
                BluetoothActionResult.Success
            },
            onDenied = { permission ->
                BluetoothActionResult.PermissionRequired(permission)
            }
        )
    }

    override fun turnOff(): BluetoothActionResult {
        val adapter = bluetoothManager.adapter
        return checkBluetoothPermission(
            onGranted = {
                adapter.disable()
                BluetoothActionResult.Success
            },
            onDenied = { permission ->
                BluetoothActionResult.PermissionRequired(permission)
            }
        )
    }

    override fun findPairedDevices(onPermissionDenied: (permission: String) -> Unit) {
        val adapter = bluetoothManager.adapter
        return checkBluetoothPermission(
            onGranted = {
                pairedDevices.value = adapter.bondedDevices.map { bluetoothDevice ->
                    BluetoothDeviceDomainEntity(bluetoothDevice.name, bluetoothDevice.address)
                }
            },
            onDenied = {
                onPermissionDenied(it)
            })
    }

    override fun discoverDevices() {

    }

    private fun <T> checkBluetoothPermission(
        onGranted: () -> T,
        onDenied: (permission: String) -> T
    ): T {
        return when (val result = permissionVerifier.check(getBluetoothPermission())) {
            PermissionVerificationResult.Granted -> {
                onGranted()
            }
            is PermissionVerificationResult.Denied -> {
                onDenied(result.permission)
            }
        }
    }

    private fun getBluetoothPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_CONNECT
    } else {
        Manifest.permission.BLUETOOTH
    }
}