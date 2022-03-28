package com.example.composebluetooth.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DefaultBluetoothService @Inject constructor(
    private val context: Context,
    private val permissionVerifier: PermissionVerifier,
    private val bluetoothStateMapper: Mapper<Int, BluetoothState>,
    private val bluetoothDeviceToDomainMapper: Mapper<BluetoothDevice, BluetoothDeviceDomainEntity>
) : BluetoothService {

    private val bluetoothConnectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_CONNECT
    } else {
        Manifest.permission.BLUETOOTH
    }

    private val bluetoothScanPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

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
                BluetoothActionResult.PermissionsRequired(permission)
            },
            bluetoothConnectPermission
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
                BluetoothActionResult.PermissionsRequired(permission)
            },
            bluetoothConnectPermission
        )
    }

    override fun fetchPairedDevices(): BluetoothActionResult {
        val adapter = bluetoothManager.adapter
        return checkBluetoothPermission(
            onGranted = {
                pairedDevices.value = adapter.bondedDevices.map { bluetoothDevice ->
                    bluetoothDeviceToDomainMapper.map(bluetoothDevice)
                }
                BluetoothActionResult.Success
            },
            onDenied = { permissions ->
                BluetoothActionResult.PermissionsRequired(permissions)
            },
            bluetoothConnectPermission
        )
    }

    override fun startDiscovery(): BluetoothActionResult {
        val adapter = bluetoothManager.adapter
        return checkBluetoothPermission(
            onGranted = {
                if (adapter.isDiscovering) {
                    adapter.cancelDiscovery()
                }
                if (adapter.startDiscovery()) {
                    BluetoothActionResult.Success
                } else {
                    BluetoothActionResult.Failure
                }
            },
            onDenied = { permissions ->
                BluetoothActionResult.PermissionsRequired(permissions)
            },
            *bluetoothScanPermissions.toTypedArray()
        )
    }

    private fun <T> checkBluetoothPermission(
        onGranted: () -> T,
        onDenied: (permissions: List<String>) -> T,
        vararg permissions: String
    ): T {
        return when (val result = permissionVerifier.check(*permissions)) {
            PermissionVerificationResult.Granted -> {
                onGranted()
            }
            is PermissionVerificationResult.Denied -> {
                onDenied(result.permissions)
            }
        }
    }
}