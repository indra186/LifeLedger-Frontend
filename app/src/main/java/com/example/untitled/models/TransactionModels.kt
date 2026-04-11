package com.example.untitled.models

// Reusing Transaction from DashboardModels if possible, but creating a specific one for list/add
// to be safe and explicit.

data class TransactionItem(
    val id: Int,
    val amount: Double,
    val type: String,
    val category: String,
    val description: String?,
    val tx_date: String,
    val created_at: String,
    val account_name: String?
)

data class TransactionDetailResponse(
    val success: Boolean,
    val message: String,
    val data: Data
) {
    data class Data(
        val id: Int,
        val amount: Double,
        val type: String,
        val category: String,
        val description: String?,
        val tx_date: String,
        val created_at: String,
        val account_name: String?
    )
}




data class TransactionsResponse(
    val success: Boolean,
    val message: String,
    val data: List<TransactionItem>?
)

data class AddTransactionRequest(
    val amount: Double,
    val type: String,
    val category: String,
    val description: String?,
    val date: String,
    val account_id: Int?
)


data class AddTransactionResponse(
    val success: Boolean,
    val message: String,
    val transaction_id: String?
)
