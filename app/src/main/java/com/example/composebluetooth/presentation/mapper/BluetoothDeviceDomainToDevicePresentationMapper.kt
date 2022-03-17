package com.example.composebluetooth.presentation.mapper

import com.example.composebluetooth.Mapper
import com.example.composebluetooth.domain.BluetoothDeviceDomainEntity
import com.example.composebluetooth.presentation.DevicePresentationEntity
import javax.inject.Inject

class BluetoothDeviceDomainToDevicePresentationMapper @Inject constructor() :
    Mapper<BluetoothDeviceDomainEntity, DevicePresentationEntity> {
    override fun map(from: BluetoothDeviceDomainEntity): DevicePresentationEntity {
        return DevicePresentationEntity(from.name, from.macAddress)
    }
}