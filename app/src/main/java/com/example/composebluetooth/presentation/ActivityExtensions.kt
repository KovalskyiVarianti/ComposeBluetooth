package com.example.composebluetooth.presentation

import android.app.Activity
import android.widget.Toast

fun Activity.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()