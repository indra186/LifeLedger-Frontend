package com.example.lifeledger.models

data class FinalInsight(
    val goalInsight: GoalInsight,
    val financeInsight: FinanceInsight,
    val behavior: String,
    val risk: String,
    val confidence: ConfidenceLevel,
    val spendingImpact: String,
    val recoveryPlan: RecoveryPlan,
)