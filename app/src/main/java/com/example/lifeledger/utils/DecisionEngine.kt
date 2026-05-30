package com.example.lifeledger.utils

import com.example.lifeledger.models.*

object DecisionEngine {

    fun prioritizeGoals(goals: List<GoalInsight>): List<GoalInsight> {

        return goals.sortedByDescending {
            val urgencyScore = when (it.state) {
                GoalState.URGENT -> 3
                GoalState.SHORT_TERM -> 2
                GoalState.NORMAL -> 1
                else -> 0
            }

            val riskScore = if (it.willMiss) 2 else 0

            val feasibilityPenalty = if (!it.achievable) -2 else 0
            val consistencyBonus = it.consistency

            val importance = urgencyScore + riskScore + feasibilityPenalty + consistencyBonus

            importance
        }
    }
    fun allocateSavings(
        totalDailyCapacity: Double,
        goals: List<GoalInsight>
    ): Map<GoalInsight, Double> {

        if (goals.isEmpty() || totalDailyCapacity <= 0) return emptyMap()

        val allocation = mutableMapOf<GoalInsight, Double>()

        // ✅ STEP 1: calculate weights ONCE
        val weights = goals.map { goal ->
            val urgency = when (goal.state) {
                GoalState.URGENT -> 3
                GoalState.SHORT_TERM -> 2
                else -> 1
            }

            val risk = if (goal.willMiss) 2 else 1

            goal to (urgency + risk)
        }

        val totalWeight = weights.sumOf { it.second }

        // ✅ STEP 2: distribute capacity
        weights.forEach { (goal, weight) ->

            val share = (weight.toDouble() / totalWeight) * totalDailyCapacity

            val assigned = minOf(share, goal.requiredPerDay)

            allocation[goal] = assigned
        }

        return allocation
    }
}