package com.example.untitled.utils

import com.example.untitled.models.*
import com.example.untitled.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AgentEngine {
    fun runAgent(
        goal: GoalInsight,
        finance: FinanceInsight,
        recovery: RecoveryPlan,
        api: ApiService,
        callback: (AgentAction) -> Unit
    ) {

        val request = OpenAIRequest(
            goal = GoalPayload(
                id = goal.goalId,
                required = goal.requiredPerDay,
                actual = goal.actualPerDay,
                remaining = goal.remaining,
                daysLeft = goal.daysLeft
            ),
            finance = FinancePayload(
                income = finance.monthlyIncome,
                expense = finance.monthlyExpense,
                topCategory = finance.topCategory,
                surplus = finance.surplus
            ),
            recovery = RecoveryPayload(
                suggestion = recovery.suggestion
            )
        )
        android.util.Log.d("AI_DEBUG", "Sending AI request: $request")

        api.getAIDecision(request)
            .enqueue(object : Callback<OpenAIResponse> {

                override fun onResponse(
                    call: Call<OpenAIResponse>,
                    response: Response<OpenAIResponse>
                ) {
                    val ai = response.body()?.data

                    if (ai != null) {
                        callback(ai)
                    } else {
                        fallback(goal, callback)
                    }
                    android.util.Log.d("AI_DEBUG", "Response: ${response.body()}")
                }

                override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                    fallback(goal, callback)
                    android.util.Log.e("AI_DEBUG", "API FAILED: ${t.message}")
                }
            })

    }

    private fun fallback(goal: GoalInsight, callback: (AgentAction) -> Unit) {

        val strategy = when {
            goal.actualPerDay == 0.0 -> "START_SAVING"
            goal.willMiss -> "CUT_SPENDING"
            else -> "OPTIMIZE_SAVING"
        }

        val action = when (strategy) {
            "START_SAVING" -> "Start saving ₹${goal.requiredPerDay.toInt()} daily"
            "CUT_SPENDING" -> "Reduce daily expenses to meet goal"
            "INCREASE_INCOME" -> "Find additional income source"
            else -> "Optimize your savings consistency"
        }

        callback(
            AgentAction(
                strategy = strategy,
                action = action,
                urgency = goal.state.toString(),
                isCritical = goal.willMiss
            )
        )
    }
}