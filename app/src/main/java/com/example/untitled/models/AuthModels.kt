package com.example.untitled.models

data class LoginRequest(
    val email: String,
    val pass: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val name: String,
    val email: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val pass: String
)

data class SignupResponse(
    val success: Boolean,
    val message: String,
    val userId: String?
)

data class SendOtpRequest(
    val email: String
)

data class SendOtpResponse(
    val success: Boolean,
    val message: String,
    val data: OtpData?
)

data class OtpData(
    val otp: String,
    val expires_at: String
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class VerifyOtpResponse(
    val success: Boolean,
    val message: String
)
