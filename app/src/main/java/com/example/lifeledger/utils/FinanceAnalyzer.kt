package com.example.lifeledger.utils
import com.example.lifeledger.models.TransactionItem
import com.example.lifeledger.models.FinanceInsight

object FinanceAnalyzer {

    fun analyze(transactions: List<TransactionItem>): FinanceInsight {

        if (transactions.isEmpty()) {
            return FinanceInsight(0.0, 0.0, 0.0, "None", 0.0, false, savingCapacity = 0.0)
        }

        val income = transactions
            .filter { it.type == "income" }
            .sumOf { it.amount }

        val expense = transactions
            .filter { it.type == "expense" }
            .sumOf { it.amount }

        val surplus = income - expense

        val categoryMap = mutableMapOf<String, Double>()

        transactions
            .filter { it.type == "expense" }
            .forEach {
                val category = it.category
                categoryMap[category] =
                    categoryMap.getOrDefault(category, 0.0) + it.amount
            }

        val top = categoryMap.maxByOrNull { it.value }

        val topCategory = top?.key ?: "None"
        val topSpend = top?.value ?: 0.0

        val isOverspending = income > 0.0 && (topSpend / income > 0.4)
        val savingCapacity = (income - expense).coerceAtLeast(0.0)

        return FinanceInsight(
            income,
            expense,
            surplus,
            topCategory,
            topSpend,
            isOverspending,
            savingCapacity
        )
    }
}