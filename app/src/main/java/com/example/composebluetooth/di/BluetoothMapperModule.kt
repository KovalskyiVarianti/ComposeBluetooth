package com.example.composebluetooth.di

import com.example.composebluetooth.Mapper
import com.example.composebluetooth.data.bluetooth.BluetoothDeviceEntity
import com.example.composebluetooth.data.bluetooth.mapper.BluetoothDeviceToDeviceDomainMapper
import com.example.composebluetooth.data.bluetooth.mapper.BluetoothStateMapper
import com.example.composebluetooth.domain.BluetoothDeviceDomainEntity
import com.example.composebluetooth.domain.BluetoothState
import com.example.composebluetooth.presentation.DevicePresentationEntity
import com.example.composebluetooth.presentation.MainUiState
import com.example.composebluetooth.presentation.mapper.BluetoothDeviceDomainToDevicePresentationMapper
import com.example.composebluetooth.presentation.mapper.BluetoothStateToMainUiStateMapper
import dagger.Binds
import dagger.Module

@Module
interface BluetoothMapperModule {
    @Binds
    fun bindBluetoothDeviceToDeviceDomainMapper(
        bluetoothDeviceToDeviceDomainMapper: BluetoothDeviceToDeviceDomainMapper
    ): Mapper<BluetoothDeviceEntity, BluetoothDeviceDomainEntity>

    @Binds
    fun bindBluetoothDeviceDomainToDevicePresentationMapper(
        bluetoothDeviceDomainToDevicePresentationMapper: BluetoothDeviceDomainToDevicePresentationMapper
    ): Mapper<BluetoothDeviceDomainEntity, DevicePresentationEntity>

    @Binds
    fun bindBluetoothStateMapper(
        bluetoothStateMapper: BluetoothStateMapper
    ): Mapper<Int, BluetoothState>

    @Binds
    fun bindBluetoothStateToMainUiStateMapper(
        bluetoothStateToMainUiStateMapper: BluetoothStateToMainUiStateMapper
    ): Mapper<BluetoothState, MainUiState>
}