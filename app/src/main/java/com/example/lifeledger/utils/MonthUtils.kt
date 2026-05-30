package com.example.lifeledger.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MonthUtils {

    fun formatMonthYear(month: Int, year: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.YEAR, year)

        return SimpleDateFormat("MMM yyyy", Locale.getDefault())
            .format(cal.time)
    }

    fun getMonthDateRange(month: Int, year: Int): Pair<String, String> {

        val startCal = Calendar.getInstance()
        startCal.set(year, month, 1)

        val endCal = Calendar.getInstance()
        endCal.set(year, month,
            endCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return Pair(
            sdf.format(startCal.time),
            sdf.format(endCal.time)
        )
    }
}