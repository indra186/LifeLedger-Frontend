package com.example.untitled.utils

import com.example.untitled.models.*

object RecoveryPlanner {

    fun generate(
        goalInsight: GoalInsight,
        finance: FinanceInsight
    ): RecoveryPlan {

        val gap = goalInsight.gap.coerceAtLeast(0.0)
        if (goalInsight.state == GoalState.COMPLETED) {
            return RecoveryPlan(0.0, 0.0, "None", "Goal already completed", true)
        }
        // 🔥 If already on track → no recovery needed
        if (!goalInsight.willMiss && goalInsight.achievable) {
            return RecoveryPlan(
                goalInsight.requiredPerDay,
                0.0,
                "None",
                "You are on track. Maintain current behavior.",
                true
            )
        }

        // 🔥 If no spending data
        if (finance.topCategory == "None") {
            return RecoveryPlan(
                goalInsight.requiredPerDay,
                gap,
                "Unknown",
                "Increase savings by ₹${gap.toInt()} daily",
                false
            )
        }
        if (goalInsight.actualPerDay == 0.0) {
            return RecoveryPlan(
                goalInsight.requiredPerDay,
                gap,
                finance.topCategory,
                "Start saving at least ₹${goalInsight.requiredPerDay.toInt()}/day. Reduce ${finance.topCategory}",
                false
            )
        }
        // 🔥 Calculate realistic cut
        val cut = when {
            finance.topCategorySpend <= 0 -> gap
            gap <= finance.topCategorySpend -> gap
            else -> finance.topCategorySpend * 0.7   // can't cut 100%
        }

        val realCapacity = finance.surplus

        val isPossible = realCapacity >= goalInsight.requiredPerDay
        if (!isPossible && finance.surplus <= 0 && goalInsight.actualPerDay == 0.0) {
            return RecoveryPlan(
                goalInsight.requiredPerDay,
                cut,
                finance.topCategory,
                "No savings capacity. You must increase income.",
                false
            )
        }
        val suggestion = when {
            isPossible ->
                "Reduce ${finance.topCategory} spending by ₹${cut.toInt()}/day to stay on track"

            else ->
                "Even after reducing ${finance.topCategory}, goal is difficult. Increase income or extend deadline"
        }

        return RecoveryPlan(
            goalInsight.requiredPerDay,
            cut,
            finance.topCategory,
            suggestion,
            isPossible
        )
    }
}