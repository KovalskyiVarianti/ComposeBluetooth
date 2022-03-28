package com.example.composebluetooth.receivers.broadcast.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

abstract class BroadcastReceiverWrapper<T>(
    private val context: T,
) : DefaultLifecycleObserver where T : Context, T : LifecycleOwner {

    protected abstract val intentFilter: IntentFilter
    protected abstract val broadcastReceiver: BroadcastReceiver

    fun startObserving() {
        context.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        context.applicationContext.registerReceiver(
            broadcastReceiver,
            intentFilter
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        context.apply {
            applicationContext.unregisterReceiver(broadcastReceiver)
            lifecycle.removeObserver(this@BroadcastReceiverWrapper)
        }
    }
}