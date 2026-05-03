package com.example.untitled.utils
import com.example.untitled.models.*

object InsightEngine {
    fun generate(
        goal: Goal,
        history: List<GoalProgress>,
        transactions: List<TransactionItem>
    ): FinalInsight {

        val goalInsight = GoalCalculator.calculateInsight(goal, history)
        val financeInsight = FinanceAnalyzer.analyze(transactions)
        val confidence = GoalCalculator.getConfidence(transactions)

        val behavior = when {
            goalInsight.state == GoalState.COMPLETED -> "Goal achiever"

            goalInsight.consistency < 0.2 && goalInsight.actualPerDay == 0.0 ->
                "Inactive saver"

            goalInsight.consistency < 0.3 ->
                "Rare saver"

            goalInsight.consistency < 0.6 && goalInsight.trend == Trend.DOWN ->
                "Losing momentum"

            goalInsight.consistency < 0.6 ->
                "Irregular saver"

            goalInsight.trend == Trend.UP ->
                "Improving saver"

            else ->
                "Disciplined saver"
        }

        val risk = when {
            goalInsight.state == GoalState.COMPLETED ->
                "🏆 Goal achieved"

            goalInsight.daysLeft < 0 && !goalInsight.achievable ->
                "❌ Missed and unrealistic to recover"

            goalInsight.daysLeft < 0 ->
                "⚠ Missed but recoverable"

            !goalInsight.achievable ->
                "🚨 Not achievable with current behavior"

            goalInsight.willMiss ->
                "⚠ Falling behind"

            else ->
                "✅ On track"
        }
        val spendingImpact = when {
            financeInsight.isOverspending && goalInsight.willMiss ->
                "💣 Spending is hurting your goal"
            financeInsight.isOverspending ->
                "⚠ High spending detected"
            else ->
                "✅ Spending under control"
        }
        val recovery = RecoveryPlanner.generate(goalInsight, financeInsight)

        return FinalInsight(
            goalInsight,
            financeInsight,
            behavior,
            risk,
            confidence,
            spendingImpact,
            recovery
        )
    }
    fun generateMultiGoal(
        goals: List<Goal>,
        historyMap: Map<String, List<GoalProgress>>,
        transactions: List<TransactionItem>
    ): MultiGoalInsight {

        val finance = FinanceAnalyzer.analyze(transactions)

        val goalInsights = goals.map { goal ->
            val history = historyMap[goal.id] ?: emptyList()
            GoalCalculator.calculateInsight(goal, history)
        }

        val prioritized = DecisionEngine.prioritizeGoals(goalInsights)

        val allocation = DecisionEngine.allocateSavings(
            finance.savingCapacity,
            prioritized
        )

        return MultiGoalInsight(
            prioritizedGoals = prioritized,
            allocation = allocation,
            totalCapacity = finance.savingCapacity
        )
    }
}
//    fun generate(
//        goal: Goal,
//        history: List<GoalProgress>,
//        transactions: List<TransactionItem>
//    ): FinalInsight {
//
//        val goalInsight = GoalCalculator.calculateInsight(goal, history)
//        val financeInsight = FinanceAnalyzer.analyze(transactions)
//        val confidence = GoalCalculator.getConfidence(transactions)
//
//        val behavior = when {
//            goalInsight.consistency < 0.3 -> "Rare saver"
//            goalInsight.consistency < 0.6 -> "Irregular saver"
//            else -> "Consistent saver"
//        }
//
//        val risk = if (goalInsight.willMiss)
//            "⚠ Likely to miss goal"
//        else
//            "✅ On track"
//
//        return FinalInsight(
//            goalInsight,
//            financeInsight,
//            behavior,
//            risk,
//            confidence
//        )
//    }
