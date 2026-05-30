package com.example.lifeledger.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String
)

data class UserData(
    val token: String?,
    @SerializedName("id") val userId: String?,
    val name: String?,
    val email: String?
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)
data class SignupResponse(
    val success: Boolean,
    val message: String,
    val data: SignupData?
)

data class SignupData(
    val email: String
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
data class UpdateEmailRequest(

    @SerializedName("user_id")
    val userId: Int,

    val email: String,

    val otp: String
)

data class BasicApiResponse(

    val success: Boolean,

    val message: String
)
data class ChangePasswordRequest(

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("current_password")
    val currentPassword: String,

    @SerializedName("new_password")
    val newPassword: String
)
data class DeleteAccountResponse(

    val success: Boolean,

    val message: String
)
data class ResetPasswordRequest(

    val email: String,

    val otp: String,

    val new_password: String
)
data class GoogleLoginRequest(
    val name: String,
    val email: String
)
