package com.example.composebluetooth.di

import android.content.Context
import com.example.composebluetooth.data.DefaultPermissionVerifier
import com.example.composebluetooth.domain.PermissionVerifier
import dagger.Module
import dagger.Provides

@Module(includes = [BluetoothModule::class])
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun providePermissionVerifier() : PermissionVerifier {
        return DefaultPermissionVerifier(context)
    }
}