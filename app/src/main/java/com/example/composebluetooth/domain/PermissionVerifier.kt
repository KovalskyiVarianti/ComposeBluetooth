package com.example.composebluetooth.domain

interface PermissionVerifier {
    fun check(vararg permissions: String): PermissionVerificationResult
}