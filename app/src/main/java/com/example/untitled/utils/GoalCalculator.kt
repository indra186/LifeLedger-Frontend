package com.example.untitled.utils

import com.example.untitled.models.*
import java.text.SimpleDateFormat
import java.util.*

object GoalCalculator {
    fun calculateInsight(
        goal: Goal,
        history: List<GoalProgress>
    ): GoalInsight {

        val remaining = (goal.target_amount - goal.current_amount).coerceAtLeast(0.0)
        if (remaining <= 0) {
            return GoalInsight(
                goal.id,
                GoalState.COMPLETED,
                0.0,
                calculateDailySaving(history),
                0.0,
                0,
                0,
                0.0,
                1.0,
                Trend.STABLE,
                false,
                true
            )
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val now = Date()

        val createdDate = try {
            sdf.parse(goal.created_at ?: "")!!
        } catch (e: Exception) {
            now
        }

        val deadlineDate = try {
            sdf.parse(goal.deadline ?: "")!!
        } catch (e: Exception) {
            now
        }
        val totalDays = ((deadlineDate.time - createdDate.time) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)

        val idealDaily = goal.target_amount / totalDays

// 🔥 TRUE DAYS LEFT
        val daysLeft = ((deadlineDate.time - now.time) / (1000 * 60 * 60 * 24)).toInt()

// 🔥 EFFECTIVE DAYS (cannot be zero)
        val effectiveDays = maxOf(daysLeft, 1)

        val state = getGoalState(daysLeft, remaining)


        val predicted = PredictionEngine.predictDailySaving(history)
        val dailyActual = if (predicted > 0) predicted else calculateDailySaving(history)

        val paceRatio = if (idealDaily > 0) dailyActual / idealDaily else 0.0

        val consistency = calculateConsistency(history)

        val trend = calculateTrend(history)
        val sorted = history.map { it.amount_added }.sortedDescending()

        val top3Avg = sorted.take(3).average().coerceAtLeast(0.0)

        val maxPossible = when {
            sorted.isEmpty() -> 0.0
            sorted.size < 3 -> sorted.average()
            else -> top3Avg
        }

        val dailyRequiredRaw = remaining / effectiveDays
        val dailyRequired = dailyRequiredRaw

//        val dailyRequired = when {
//            dailyActual == 0.0 -> idealDaily   // fallback plan
//            dailyRequiredRaw > maxPossible -> maxPossible
//            else -> dailyRequiredRaw
//        }
//        val achievable = when {
//            dailyActual == 0.0 -> false
//            dailyRequired <= maxPossible -> true
//            else -> false
//        }

        val achievable = dailyActual >= dailyRequiredRaw * 0.8
//        val willMiss = (dailyActual < dailyRequiredRaw) || (paceRatio < 0.7)
        val willMiss = dailyActual < dailyRequiredRaw

        val recoveryDays = if (dailyActual > 0)
            (remaining / dailyActual).toInt()
        else 30

        val gap = dailyRequired - dailyActual
        if (daysLeft < 0) {
            return GoalInsight(
                goal.id,
                GoalState.OVERDUE,
                remaining,
                dailyActual,
                remaining - dailyActual,
                0,
                daysLeft,
                remaining,
                0.0,
                Trend.STABLE,
                true,
                false
            )
        }else {
            return GoalInsight(
                goal.id,
                state,
                dailyRequired,
                dailyActual,
                gap,
                recoveryDays,
                daysLeft,
                remaining,
                consistency,
                trend,
                willMiss,
                achievable
            )
        }
    }
    private fun calculateConsistency(history: List<GoalProgress>): Double {

        if (history.isEmpty()) return 0.0

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Date()

        val last10Days = history.filter {
            val date = sdf.parse(it.date_added) ?: return@filter false
            val diff = now.time - date.time
            val days = diff / (1000 * 60 * 60 * 24)
            days <= 10
        }

        val activeDays = last10Days.size

        return activeDays / 10.0  // value between 0–1
    }
    private fun calculateTrend(history: List<GoalProgress>): Trend {

        if (history.size < 4) return Trend.STABLE

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Date()

        val last7 = history.filter {
            val d = sdf.parse(it.date_added) ?: return@filter false
            (now.time - d.time) / (1000 * 60 * 60 * 24) <= 7
        }

        val prev7 = history.filter {
            val d = sdf.parse(it.date_added) ?: return@filter false
            val days = (now.time - d.time) / (1000 * 60 * 60 * 24)
            days in 8..14
        }

        val lastAvg = last7.sumOf { it.amount_added } / (last7.size.coerceAtLeast(1))
        val prevAvg = prev7.sumOf { it.amount_added } / (prev7.size.coerceAtLeast(1))

        return when {
            lastAvg > prevAvg -> Trend.UP
            lastAvg < prevAvg -> Trend.DOWN
            else -> Trend.STABLE
        }
    }
    private fun getGoalState(daysLeft: Int, remaining: Double): GoalState {
        return when {
            remaining <= 0 -> GoalState.COMPLETED
            daysLeft < 0 -> GoalState.OVERDUE
            daysLeft in 0..3 -> GoalState.URGENT
            daysLeft in 4..7 -> GoalState.SHORT_TERM
            else -> GoalState.NORMAL
        }
    }
    private fun calculateDailySaving(history: List<GoalProgress>): Double {

        if (history.isEmpty()) return 0.0

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val now = Date()

            val last7Days = history.filter {
                val date = sdf.parse(it.date_added) ?: return@filter false
                val diff = now.time - date.time
                val days = diff / (1000 * 60 * 60 * 24)
                days <= 7
            }

            if (last7Days.isEmpty()) return 0.0

            // ✅ FIX: early users
            if (last7Days.size < 3) {
                return last7Days.sumOf { it.amount_added } / last7Days.size
            }

            val total = last7Days.sumOf { it.amount_added }
            val activeDays = last7Days.size
            val consistency = activeDays / 7.0

            val baseDaily = total / activeDays

            baseDaily * consistency

        } catch (e: Exception) {
            0.0
        }
    }
    fun getConfidence(list: List<TransactionItem>): ConfidenceLevel {

        if (list.isEmpty()) return ConfidenceLevel.LOW

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val now = Date()

        val parsedDates = list.mapNotNull {
            try { sdf.parse(it.tx_date) } catch (e: Exception) { null }
        }

        if (parsedDates.isEmpty()) return ConfidenceLevel.LOW

        val lastDate = parsedDates.maxByOrNull { it.time }!!
        val firstDate = parsedDates.minByOrNull { it.time }!!

        val daysGap = (now.time - lastDate.time) / (1000 * 60 * 60 * 24)
        val spanDays = ((lastDate.time - firstDate.time) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)

        val size = list.size
        val density = size.toDouble() / spanDays   // transactions per day

        return when {
            size >= 20 && daysGap <= 1 && density > 0.5 -> ConfidenceLevel.HIGH
            size >= 10 && daysGap <= 3 && density > 0.3 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }
    }
}