package com.example.composebluetooth.receivers.broadcast.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class BluetoothStateBroadcastReceiver<T>(
    context: T,
    onStateOn: () -> Unit,
    onStateOff: () -> Unit,
    onStateTurningOn: () -> Unit,
    onStateTurningOff: () -> Unit,
) : BroadcastReceiverWrapper<T>(context) where T : Context, T : LifecycleOwner {

    override val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)

    override val broadcastReceiver = object : BroadcastReceiver() {
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
}