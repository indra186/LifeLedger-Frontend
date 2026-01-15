package com.example.untitled.models

data class Account(
    val id: Int,
    val account_name: String,
    val type: String,
    val balance: Double
)

data class AccountsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Account>
)

data class AddAccountRequest(
    val name: String,
    val type: String,
    val balance: Double
)

data class AddAccountResponse(
    val success: Boolean,
    val message: String,
    val account_id: Int?
)
