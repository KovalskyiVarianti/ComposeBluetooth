package com.example.composebluetooth.data.bluetooth.mapper

import com.example.composebluetooth.domain.BluetoothDeviceDomainEntity
import com.example.composebluetooth.Mapper
import com.example.composebluetooth.data.bluetooth.BluetoothDeviceEntity
import javax.inject.Inject

class BluetoothDeviceToDeviceDomainMapper @Inject constructor() : Mapper<BluetoothDeviceEntity, BluetoothDeviceDomainEntity> {
    override fun map(from: BluetoothDeviceEntity): BluetoothDeviceDomainEntity {
        return BluetoothDeviceDomainEntity(from.name, from.macAddress)
    }
}