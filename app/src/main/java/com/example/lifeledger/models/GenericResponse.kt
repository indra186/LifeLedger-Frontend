package com.example.lifeledger.models

data class GenericResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)