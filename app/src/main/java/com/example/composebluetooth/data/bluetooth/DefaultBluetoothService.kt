package com.example.composebluetooth.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.*
import javax.inject.Inject

class DefaultBluetoothService @Inject constructor(
    private val context: Context,
    private val permissionVerifier: PermissionVerifier,
    private val bluetoothStateMapper: Mapper<Int, BluetoothState>
) : BluetoothService {

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

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

    override fun getPairedDevices(onPermissionDenied: (permission: String) -> Unit): List<BluetoothDeviceDomainEntity> {
        val adapter = bluetoothManager.adapter
        return checkBluetoothPermission({
            adapter.bondedDevices.map {
                BluetoothDeviceDomainEntity(it.name, it.address)
            }
        }, {
            onPermissionDenied(it)
            emptyList()
        })
    }

    private fun BluetoothAdapter.isDisabled() = !isEnabled

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