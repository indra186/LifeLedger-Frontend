package com.example.untitled.models

// Reusing Transaction from DashboardModels if possible, but creating a specific one for list/add
// to be safe and explicit.

data class TransactionItem(
    val id: String,
    val title: String,
    val amount: Double,
    val type: String, // "income" or "expense"
    val category: String,
    val date: String,
    val notes: String?
)

data class TransactionsResponse(
    val success: Boolean,
    val message: String,
    val data: List<TransactionItem>?
)

data class AddTransactionRequest(
    val user_id: String,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String,
    val notes: String?
)

data class AddTransactionResponse(
    val success: Boolean,
    val message: String,
    val transaction_id: String?
)
