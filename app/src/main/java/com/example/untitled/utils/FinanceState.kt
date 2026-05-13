package com.example.untitled.utils

import com.example.untitled.models.AvailableMonth
import java.util.Calendar

object FinanceState {

    var selectedMonth =
        Calendar.getInstance().get(Calendar.MONTH)

    var selectedYear =
        Calendar.getInstance().get(Calendar.YEAR)

    var firstTransactionMonth = -1

    var firstTransactionYear = -1
}
object BudgetState {

    var selectedMonth =
        Calendar.getInstance().get(Calendar.MONTH)

    var selectedYear =
        Calendar.getInstance().get(Calendar.YEAR)

    var availableMonths: List<AvailableMonth> =
        emptyList()
}