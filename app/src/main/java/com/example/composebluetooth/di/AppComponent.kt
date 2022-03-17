package com.example.composebluetooth.di

import com.example.composebluetooth.presentation.MainActivity
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}