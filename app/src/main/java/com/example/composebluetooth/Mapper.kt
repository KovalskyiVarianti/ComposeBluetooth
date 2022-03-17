package com.example.composebluetooth

interface Mapper<F, T> {
    fun map(from: F): T
}