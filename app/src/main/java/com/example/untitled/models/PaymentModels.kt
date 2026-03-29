package com.example.untitled.models

data class PaymentRequest(
    val amount: Double,
    val method: String,
    val gateway_payment_id: String
)

data class CardDetails(
    val number: String,
    val expiry: String,
    val cvv: String,
    val holder: String
)
