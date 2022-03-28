package com.example.composebluetooth.domain

sealed interface PermissionVerificationResult {
    object Granted : PermissionVerificationResult
    data class Denied(val permissions: List<String>) : PermissionVerificationResult
}