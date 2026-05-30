package com.example.lifeledger.models


data class AgentFeedbackRequest(
    val goal_id: String,
    val strategy: String,
    val actual_saved: Double,
    val expected_saved: Double,
    val success: Boolean
)
data class StrategyScoreResponse(
    val strategy: String,
    val score: Double
)