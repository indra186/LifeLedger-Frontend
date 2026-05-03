package com.example.untitled.models

data class RecoveryPlan(
    val requiredPerDay: Double,
    val suggestedCut: Double,
    val category: String,
    val suggestion: String,
    val isPossible: Boolean
)