package com.example.untitled

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.untitled.databinding.FragmentMonthlyReportBinding
import com.example.untitled.models.MonthlyReportResponse
import com.example.untitled.network.RetrofitClient
import com.example.untitled.utils.FinanceState
import com.example.untitled.utils.MonthUtils
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.untitled.models.AvailableMonthsResponse
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MonthlyReportFragment : Fragment() {

    private var _binding:
            FragmentMonthlyReportBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        _binding =
            FragmentMonthlyReportBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        binding.ivBack.setOnClickListener {

            requireActivity().onBackPressed()
        }

        setupMonthSelector()
        loadAvailableMonths()
        loadMonthlyReport()
    }

    /*
    |--------------------------------------------------------------------------
    | MONTH SELECTOR
    |--------------------------------------------------------------------------
    */

    private fun setupMonthSelector() {

        updateMonthText()

        binding.btnPrevMonth.setOnClickListener {

            if(!binding.btnPrevMonth.isEnabled)
                return@setOnClickListener

            FinanceState.selectedMonth--

            if(FinanceState.selectedMonth < 0) {

                FinanceState.selectedMonth = 11
                FinanceState.selectedYear--
            }

            updateMonthText()
            updateMonthButtons()
            loadMonthlyReport()
        }

        binding.btnNextMonth.setOnClickListener {

            if(!binding.btnNextMonth.isEnabled)
                return@setOnClickListener
            val current =
                Calendar.getInstance()

            val currentMonth =
                current.get(Calendar.MONTH)

            val currentYear =
                current.get(Calendar.YEAR)

            if(
                FinanceState.selectedMonth == currentMonth &&
                FinanceState.selectedYear == currentYear
            ) {
                return@setOnClickListener
            }

            FinanceState.selectedMonth++

            if(FinanceState.selectedMonth > 11) {

                FinanceState.selectedMonth = 0
                FinanceState.selectedYear++
            }

            updateMonthText()
            updateMonthButtons()
            loadMonthlyReport()
        }
    }
    private fun loadAvailableMonths() {

        RetrofitClient.instance
            .getAvailableTransactionMonths()
            .enqueue(object :
                retrofit2.Callback<AvailableMonthsResponse> {

                override fun onResponse(
                    call: retrofit2.Call<AvailableMonthsResponse>,
                    response: retrofit2.Response<AvailableMonthsResponse>
                ) {

                    FinanceState.availableMonths =
                        response.body()?.data ?: emptyList()

                    updateMonthButtons()
                }

                override fun onFailure(
                    call: retrofit2.Call<AvailableMonthsResponse>,
                    t: Throwable
                ) {

                }
            })
    }

    private fun updateMonthText() {

        binding.tvSelectedMonth.text =

            MonthUtils.formatMonthYear(

                FinanceState.selectedMonth,
                FinanceState.selectedYear
            )
    }
    private fun updateMonthButtons() {

        val currentCalendar =
            Calendar.getInstance()

        val currentMonth =
            currentCalendar.get(Calendar.MONTH)

        val currentYear =
            currentCalendar.get(Calendar.YEAR)

        /*
        |--------------------------------------------------------------------------
        | NEXT BUTTON
        |--------------------------------------------------------------------------
        */

        val isCurrentMonth =

            FinanceState.selectedMonth == currentMonth &&

                    FinanceState.selectedYear == currentYear

        binding.btnNextMonth.isEnabled =
            !isCurrentMonth

        binding.btnNextMonth.alpha =
            if(isCurrentMonth) 0.3f
            else 1f

        /*
        |--------------------------------------------------------------------------
        | PREVIOUS BUTTON
        |--------------------------------------------------------------------------
        */

        val hasPreviousMonth =

            FinanceState.availableMonths.any {

                val month =
                    it.month - 1

                val year =
                    it.year

                year < FinanceState.selectedYear ||

                        (
                                year == FinanceState.selectedYear &&
                                        month < FinanceState.selectedMonth
                                )
            }

        binding.btnPrevMonth.isEnabled =
            hasPreviousMonth

        binding.btnPrevMonth.alpha =
            if(hasPreviousMonth) 1f
            else 0.3f
    }

    /*
    |--------------------------------------------------------------------------
    | LOAD REPORT
    |--------------------------------------------------------------------------
    */

    private fun loadMonthlyReport() {

        binding.progressBar.visibility =
            View.VISIBLE

        RetrofitClient.instance
            .getMonthlyReport(

                FinanceState.selectedMonth + 1,

                FinanceState.selectedYear
            )

            .enqueue(object :
                Callback<MonthlyReportResponse> {

                override fun onResponse(
                    call: Call<MonthlyReportResponse>,
                    response: Response<MonthlyReportResponse>
                ) {

                    binding.progressBar.visibility =
                        View.GONE
                    android.util.Log.d(
                        "MONTHLY_REPORT",
                        response.body().toString()
                    )

                    if(
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        val data =
                            response.body()!!.data

                        /*
                        |--------------------------------------------------------------------------
                        | SUMMARY
                        |--------------------------------------------------------------------------
                        */

                        binding.tvIncome.text =
                            "₹%.2f".format(
                                data.summary.income
                            )

                        binding.tvExpense.text =
                            "₹%.2f".format(
                                data.summary.expense
                            )

                        binding.tvSavings.text =
                            "₹%.2f".format(
                                data.summary.savings
                            )

                        binding.tvTopCategory.text =
                            data.summary.top_category
                        /*
|--------------------------------------------------------------------------
| PIE CHART
|--------------------------------------------------------------------------
*/

                        val pieEntries =
                            ArrayList<PieEntry>()

                        data.categories.forEach {

                            pieEntries.add(

                                PieEntry(
                                    it.total.toFloat(),
                                    it.category
                                )
                            )
                        }

                        val pieDataSet =
                            PieDataSet(
                                pieEntries,
                                ""
                            )

                        pieDataSet.colors =
                            listOf(
                                Color.parseColor("#FF7043"),
                                Color.parseColor("#42A5F5"),
                                Color.parseColor("#66BB6A"),
                                Color.parseColor("#AB47BC"),
                                Color.parseColor("#FFA726"),
                                Color.parseColor("#EC407A")
                            )

                        pieDataSet.valueTextColor =
                            Color.WHITE

                        pieDataSet.valueTextSize =
                            11f

                        pieDataSet.sliceSpace =
                            3f

                        pieDataSet.selectionShift =
                            5f

                        val pieData =
                            PieData(pieDataSet)

                        binding.pieChart.data =
                            pieData

                        binding.pieChart.description.isEnabled =
                            false

                        binding.pieChart.legend.isEnabled =
                            false

                        binding.pieChart.setEntryLabelColor(
                            Color.WHITE
                        )

                        binding.pieChart.setEntryLabelTextSize(
                            10f
                        )

                        binding.pieChart.holeRadius =
                            62f

                        binding.pieChart.transparentCircleRadius =
                            66f

                        binding.pieChart.setHoleColor(
                            Color.parseColor("#1E1E1E")
                        )

                        binding.pieChart.centerText =
                            "Categories"
                        binding.pieChart.setOnChartValueSelectedListener(

                            object :
                                com.github.mikephil.charting.listener
                                .OnChartValueSelectedListener {

                                override fun onValueSelected(

                                    e: com.github.mikephil.charting.data.Entry?,
                                    h: com.github.mikephil.charting.highlight.Highlight?

                                ) {

                                    if(h == null)
                                        return

                                    val index =
                                        h.x.toInt()

                                    val category =
                                        data.categories[index]

                                    val budgetLimit =
                                        category.budget_limit

                                    val spent =
                                        category.total

                                    val usage =
                                        category.usage_percent

                                    /*
                                    |--------------------------------------------------------------------------
                                    | CENTER TEXT
                                    |--------------------------------------------------------------------------
                                    */

                                    val centerText =

                                        if(budgetLimit > 0) {

                                            "${category.category}\n" +
                                                    "₹${spent.toInt()} / ₹${budgetLimit.toInt()}\n" +
                                                    "$usage% used"

                                        } else {

                                            "${category.category}\n" +
                                                    "₹${spent.toInt()} spent\n" +
                                                    "No budget set"
                                        }

                                    binding.pieChart.centerText =
                                        centerText

                                    /*
                                    |--------------------------------------------------------------------------
                                    | RISK COLORS
                                    |--------------------------------------------------------------------------
                                    */

                                    when {

                                        usage >= 100 -> {

                                            binding.pieChart.setCenterTextColor(
                                                Color.parseColor("#EF5350")
                                            )
                                        }

                                        usage >= 80 -> {

                                            binding.pieChart.setCenterTextColor(
                                                Color.parseColor("#FFA726")
                                            )
                                        }

                                        else -> {

                                            binding.pieChart.setCenterTextColor(
                                                Color.parseColor("#66BB6A")
                                            )
                                        }
                                    }

                                    binding.pieChart.invalidate()
                                }

                                override fun onNothingSelected() {

                                    binding.pieChart.centerText =
                                        "Categories"

                                    binding.pieChart.setCenterTextColor(
                                        Color.WHITE
                                    )

                                    binding.pieChart.invalidate()
                                }
                            }
                        )

                        binding.pieChart.setCenterTextColor(
                            Color.WHITE
                        )

                        binding.pieChart.animateY(1000)

                        binding.pieChart.invalidate()

//                        /*
//                        |--------------------------------------------------------------------------
//                        | PIE CHART
//                        |--------------------------------------------------------------------------
//                        */
//
//                        val pieEntries =
//                            ArrayList<PieEntry>()
//
//                        data.categories.forEach {
//
//                            pieEntries.add(
//
//                                PieEntry(
//                                    it.total.toFloat(),
//                                    it.category
//                                )
//                            )
//                        }
//
//                        val pieDataSet =
//                            PieDataSet(
//                                pieEntries,
//                                ""
//                            )
//
//                        pieDataSet.colors =
//                            listOf(
//                                Color.parseColor("#FF7043"),
//                                Color.parseColor("#42A5F5"),
//                                Color.parseColor("#66BB6A"),
//                                Color.parseColor("#AB47BC"),
//                                Color.parseColor("#FFA726")
//                            )
//
//                        pieDataSet.valueTextColor =
//                            Color.WHITE
//
//                        val pieData =
//                            PieData(pieDataSet)
//
//                        binding.pieChart.data =
//                            pieData
//
//                        binding.pieChart.description.isEnabled =
//                            false
//
//                        binding.pieChart.centerText =
//                            "Categories"
//
//                        binding.pieChart.animateY(1000)
//
//                        binding.pieChart.invalidate()

                        /*
 |--------------------------------------------------------------------------
 | BAR CHART
 |--------------------------------------------------------------------------
 */

                        val barEntries =
                            ArrayList<BarEntry>()

                        val labels =
                            ArrayList<String>()

                        data.spending_pattern.forEachIndexed {

                                index,
                                item ->

                            barEntries.add(

                                BarEntry(
                                    index.toFloat(),
                                    item.total.toFloat()
                                )
                            )

                            labels.add(item.label)
                        }

                        val barDataSet =
                            BarDataSet(
                                barEntries,
                                ""
                            )

                        barDataSet.color =
                            Color.parseColor("#FF7043")

                        barDataSet.valueTextColor =
                            Color.WHITE

                        barDataSet.valueTextSize =
                            10f

                        val barData =
                            BarData(barDataSet)

                        barData.barWidth =
                            0.55f

                        binding.barChart.data =
                            barData

                        binding.barChart.description.isEnabled =
                            false

                        binding.barChart.legend.isEnabled =
                            false

                        binding.barChart.setFitBars(true)

                        binding.barChart.setScaleEnabled(false)

                        binding.barChart.setPinchZoom(false)

                        binding.barChart.isDoubleTapToZoomEnabled =
                            false

                        binding.barChart.isDragEnabled =
                            true

                        binding.barChart.setVisibleXRangeMaximum(7f)

                        /*
                        |--------------------------------------------------------------------------
                        | AUTO SCROLL TO CURRENT DAY
                        |--------------------------------------------------------------------------
                        */

                        binding.barChart.moveViewToX(
                            (labels.size - 1).toFloat()
                        )

                        val xAxis =
                            binding.barChart.xAxis

                        xAxis.position =
                            com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

                        xAxis.valueFormatter =
                            com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                                labels
                            )

                        xAxis.granularity =
                            1f

                        xAxis.labelCount =
                            labels.size

                        xAxis.textColor =
                            Color.WHITE

                        xAxis.labelRotationAngle =
                            -45f

                        xAxis.setDrawGridLines(false)

                        xAxis.setAvoidFirstLastClipping(true)

                        binding.barChart.axisLeft.textColor =
                            Color.WHITE

                        binding.barChart.axisLeft.axisMinimum =
                            0f

                        binding.barChart.axisRight.isEnabled =
                            false

                        binding.barChart.setExtraBottomOffset(
                            18f
                        )

                        binding.barChart.animateY(1000)

                        binding.barChart.invalidate()
                    }
                }

                override fun onFailure(
                    call: Call<MonthlyReportResponse>,
                    t: Throwable
                ) {

                    binding.progressBar.visibility =
                        View.GONE
                }
            })
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}