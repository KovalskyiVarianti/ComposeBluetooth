package com.example.composebluetooth.receivers.broadcast.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LifecycleOwner
import com.example.composebluetooth.domain.BluetoothDeviceDomainEntity

class BluetoothDevicesBroadcastReceiver<T>(
    context: T,
    onDeviceFound: (BluetoothDeviceDomainEntity) -> Unit,
    onDiscoveryStarted: () -> Unit,
    onDiscoveryFinished: () -> Unit
) : BroadcastReceiverWrapper<T>(context) where T : Context, T : LifecycleOwner {

    override val intentFilter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    override val broadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        ?.let { bluetoothDevice ->
                            onDeviceFound(
                                BluetoothDeviceDomainEntity(
                                    bluetoothDevice.name,
                                    bluetoothDevice.address
                                )
                            )
                        }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    onDiscoveryStarted()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    onDiscoveryFinished()
                }
            }
        }
    }
}