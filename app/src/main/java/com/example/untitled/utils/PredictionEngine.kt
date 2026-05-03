package com.example.untitled.utils

import com.example.untitled.models.*

object PredictionEngine {

    fun predictDailySaving(history: List<GoalProgress>): Double {

        if (history.size < 5) return 0.0

        val values = history.map { it.amount_added }

        val n = values.size
        val x = (1..n).map { it.toDouble() }

        val meanX = x.average()
        val meanY = values.average()

        val numerator = x.zip(values).sumOf { (xi, yi) ->
            (xi - meanX) * (yi - meanY)
        }

        val denominator = x.sumOf { (it - meanX) * (it - meanX) }

        val slope = if (denominator == 0.0) 0.0 else numerator / denominator

        val nextX = n + 1

        return (meanY + slope * (nextX - meanX)).coerceAtLeast(0.0)
    }
}