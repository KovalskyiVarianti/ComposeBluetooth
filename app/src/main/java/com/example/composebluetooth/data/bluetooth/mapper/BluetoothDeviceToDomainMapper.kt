package com.example.composebluetooth.data.bluetooth.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.BluetoothDeviceDomainEntity
import javax.inject.Inject

class BluetoothDeviceToDomainMapper @Inject constructor() :
    Mapper<BluetoothDevice, BluetoothDeviceDomainEntity> {
    @SuppressLint("MissingPermission")
    override fun map(from: BluetoothDevice): BluetoothDeviceDomainEntity {
        return BluetoothDeviceDomainEntity(from.name, from.address)
    }
}