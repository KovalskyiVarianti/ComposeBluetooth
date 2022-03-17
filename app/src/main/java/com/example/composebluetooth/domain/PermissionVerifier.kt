package com.example.composebluetooth.domain

interface PermissionVerifier {
    fun check(permission: String): PermissionVerificationResult
}