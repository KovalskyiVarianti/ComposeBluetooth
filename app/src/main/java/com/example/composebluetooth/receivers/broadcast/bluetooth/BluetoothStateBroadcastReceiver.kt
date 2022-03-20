package com.example.composebluetooth.receivers.broadcast.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class BluetoothStateBroadcastReceiver(
    private val context: Context,
    onStateOn: () -> Unit,
    onStateOff: () -> Unit,
    onStateTurningOn: () -> Unit,
    onStateTurningOff: () -> Unit,
) : DefaultLifecycleObserver {

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_ON -> {
                        onStateOn()
                    }
                    BluetoothAdapter.STATE_OFF -> {
                        onStateOff()
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        onStateTurningOn()
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        onStateTurningOff()
                    }
                }
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        context.applicationContext.registerReceiver(
            bluetoothReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        context.applicationContext.unregisterReceiver(bluetoothReceiver)
    }
}