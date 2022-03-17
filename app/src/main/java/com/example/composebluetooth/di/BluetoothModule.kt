package com.example.composebluetooth.di

import com.example.composebluetooth.data.bluetooth.*
import com.example.composebluetooth.domain.BluetoothService
import dagger.Binds
import dagger.Module

@Module(includes = [BluetoothMapperModule::class])
interface BluetoothModule {

    @Binds
    fun bindBluetoothService(
        bluetoothService: DefaultBluetoothService
    ): BluetoothService
}