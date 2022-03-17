package com.example.composebluetooth.data

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.composebluetooth.domain.PermissionVerificationResult
import com.example.composebluetooth.domain.PermissionVerifier

class DefaultPermissionVerifier(private val context: Context) : PermissionVerifier {
    override fun check(permission: String): PermissionVerificationResult {
        return when (ActivityCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> PermissionVerificationResult.Granted
            else -> PermissionVerificationResult.Denied(permission)
        }
    }
}