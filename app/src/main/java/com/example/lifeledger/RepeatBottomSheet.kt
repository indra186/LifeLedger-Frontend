package com.example.lifeledger

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RepeatBottomSheet(
    private val onRepeatSelected:
        (Pair<String, String>) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val dialog =
            super.onCreateDialog(savedInstanceState)

        val view =
            LayoutInflater.from(context)
                .inflate(
                    R.layout.bottom_sheet_repeat,
                    null
                )

        dialog.setContentView(view)

        val btnNoRepeat =
            view.findViewById<Button>(
                R.id.btn_no_repeat
            )

        val btnDaily =
            view.findViewById<Button>(
                R.id.btn_daily
            )

        val btnWeekly =
            view.findViewById<Button>(
                R.id.btn_weekly
            )

        val btnMonthly =
            view.findViewById<Button>(
                R.id.btn_monthly
            )

        val weeklyLayout =
            view.findViewById<LinearLayout>(
                R.id.layout_week_days
            )

        val btnSave =
            view.findViewById<Button>(
                R.id.btn_save_repeat
            )

        btnNoRepeat.setOnClickListener {

            onRepeatSelected(
                Pair("none", "")
            )

            dismiss()
        }

        btnDaily.setOnClickListener {

            onRepeatSelected(
                Pair("daily", "")
            )

            dismiss()
        }

        btnMonthly.setOnClickListener {

            onRepeatSelected(
                Pair("monthly", "")
            )

            dismiss()
        }

        btnWeekly.setOnClickListener {

            weeklyLayout.visibility =
                android.view.View.VISIBLE
        }

        btnSave.setOnClickListener {

            val days =
                mutableListOf<String>()

            val ids = listOf(
                R.id.cb_mon,
                R.id.cb_tue,
                R.id.cb_wed,
                R.id.cb_thu,
                R.id.cb_fri,
                R.id.cb_sat,
                R.id.cb_sun
            )

            val names = listOf(
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat",
                "Sun"
            )

            ids.forEachIndexed { index, id ->

                val cb =
                    view.findViewById<CheckBox>(id)

                if(cb.isChecked) {

                    days.add(names[index])
                }
            }

            onRepeatSelected(
                Pair(
                    "weekly",
                    days.joinToString(",")
                )
            )

            dismiss()
        }

        return dialog
    }
}