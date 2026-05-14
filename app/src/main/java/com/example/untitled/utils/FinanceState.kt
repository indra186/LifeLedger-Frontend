package com.example.untitled.utils

import com.example.untitled.models.AvailableMonth
import java.util.Calendar

object FinanceState {

    var selectedMonth =
        Calendar.getInstance().get(Calendar.MONTH)

    var selectedYear =
        Calendar.getInstance().get(Calendar.YEAR)

    var availableMonths: List<AvailableMonth> =
        emptyList()
}
object BudgetState {

    var selectedMonth =
        Calendar.getInstance().get(Calendar.MONTH)

    var selectedYear =
        Calendar.getInstance().get(Calendar.YEAR)

    var availableMonths: List<AvailableMonth> =
        emptyList()
}