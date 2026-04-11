package com.example.untitled.models

data class GenericResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)
