package com.example.lifeledger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifeledger.adapters.BudgetsAdapter
import com.example.lifeledger.databinding.FragmentBudgetsBinding
import android.util.Log
import com.example.lifeledger.models.AvailableMonthsResponse
import com.example.lifeledger.models.BudgetsResponse
import com.example.lifeledger.network.RetrofitClient
import com.example.lifeledger.utils.MonthUtils
import java.util.Calendar
import com.example.lifeledger.utils.BudgetState


class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetsAdapter: BudgetsAdapter
    private lateinit var pbLoading: ProgressBar
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pbLoading = binding.pbLoading
        layoutEmptyState = binding.layoutEmptyState

        budgetsAdapter = BudgetsAdapter(emptyList()) { budget ->

            val bundle = Bundle()

            bundle.putString("budget_id", budget.id)
            bundle.putString("category", budget.category)
            bundle.putString("limit_amount", budget.limit_amount)
            bundle.putString("spent_amount", budget.spent_amount)
            bundle.putInt("month", budget.month)
            bundle.putInt("year", budget.year)

            findNavController().navigate(
                R.id.action_budgetsFragment_to_budgetDetailFragment,
                bundle
            )
        }

        binding.rvBudgets.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvBudgets.adapter = budgetsAdapter

        binding.btnAddBudget.setOnClickListener {

            if (!binding.btnAddBudget.isEnabled)
                return@setOnClickListener

            findNavController().navigate(
                R.id.action_budgetsFragment_to_createBudgetFragment
            )
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        setupMonthSelector()
        loadAvailableMonths()
    }
    private fun loadBudgetsByMonth() {

    RetrofitClient.instance.getBudgetsByMonth(
        BudgetState.selectedMonth + 1,
        BudgetState.selectedYear
    ).enqueue(object : retrofit2.Callback<BudgetsResponse> {

            override fun onResponse(
                call: retrofit2.Call<BudgetsResponse>,
                response: retrofit2.Response<BudgetsResponse>
            ) {

                pbLoading.visibility = View.GONE
                Log.d(
                    "BUDGET_FILTER",
                    "Month = ${BudgetState.selectedMonth + 1}, Year = ${BudgetState.selectedYear}"
                )

                Log.d(
                    "BUDGET_FILTER",
                    "Response = ${response.body()}"
                )

                val budgets = response.body()?.data ?: emptyList()

                if (budgets.isEmpty()) {

                    binding.rvBudgets.visibility = View.GONE
                    layoutEmptyState.visibility = View.VISIBLE

                    binding.tvTotalBudgetVal.text = "₹0"
                    binding.tvSpentVal.text = "You've spent ₹0"
                    binding.tvRemainingVal.text = "₹0 left"

                } else {

                    binding.rvBudgets.visibility = View.VISIBLE
                    layoutEmptyState.visibility = View.GONE

                    budgetsAdapter.updateBudgets(budgets)

                    var totalBudget = 0.0
                    var totalSpent = 0.0

                    budgets.forEach {

                        totalBudget += it.limit_amount.toDouble()
                        totalSpent += it.spent_amount.toDouble()
                    }

                    binding.tvTotalBudgetVal.text = "₹$totalBudget"
                    binding.tvSpentVal.text = "You've spent ₹$totalSpent"
                    binding.tvRemainingVal.text =
                        "₹${totalBudget - totalSpent} left"
                    val totalProgress =
                        if (totalBudget > 0)
                            ((totalSpent / totalBudget) * 100).toInt()
                        else
                            0

                    binding.pbTotalBudget.progress =
                        totalProgress
                }

                updateMonthButtons()


            }

            override fun onFailure(
                call: retrofit2.Call<BudgetsResponse>,
                t: Throwable
            ) {

            }
        })
    }
    private fun updateAddBudgetButtonState() {

        val calendar = Calendar.getInstance()

        val currentMonth =
            calendar.get(Calendar.MONTH)

        val currentYear =
            calendar.get(Calendar.YEAR)

        val isCurrentMonth =
            BudgetState.selectedMonth == currentMonth &&
                    BudgetState.selectedYear == currentYear

        binding.btnAddBudget.isEnabled =
            isCurrentMonth

        binding.btnAddBudget.alpha =
            if (isCurrentMonth) 1f else 0.5f
    }
    private fun setupMonthSelector() {

        updateMonthText()

        binding.btnPrevMonth.setOnClickListener {

            val previousMonths =
                BudgetState.availableMonths.filter {

                    val budgetMonth = it.month - 1
                    val budgetYear = it.year

                    budgetYear < BudgetState.selectedYear ||

                            (
                                    budgetYear == BudgetState.selectedYear &&
                                            budgetMonth < BudgetState.selectedMonth
                                    )
                }

            if (previousMonths.isNotEmpty()) {

                val latestPrevious =
                    previousMonths.last()

                BudgetState.selectedMonth =
                    latestPrevious.month - 1

                BudgetState.selectedYear =
                    latestPrevious.year

                updateMonthText()
                updateMonthButtons()
                loadBudgetsByMonth()
            }
        }

        binding.btnNextMonth.setOnClickListener {

            val calendar = Calendar.getInstance()

            val currentMonth =
                calendar.get(Calendar.MONTH)

            val currentYear =
                calendar.get(Calendar.YEAR)

            val nextMonths =
                BudgetState.availableMonths.filter {

                    val budgetMonth = it.month - 1
                    val budgetYear = it.year

                    budgetYear > BudgetState.selectedYear ||

                            (
                                    budgetYear == BudgetState.selectedYear &&
                                            budgetMonth > BudgetState.selectedMonth
                                    )
                }

            if (nextMonths.isNotEmpty()) {

                val next =
                    nextMonths.first()

                BudgetState.selectedMonth =
                    next.month - 1

                BudgetState.selectedYear =
                    next.year

            } else {

                BudgetState.selectedMonth =
                    currentMonth

                BudgetState.selectedYear =
                    currentYear
            }

            updateMonthText()
            updateMonthButtons()
            loadBudgetsByMonth()
        }
    }
    private fun updateMonthButtons() {

        val calendar = Calendar.getInstance()

        val currentMonth =
            calendar.get(Calendar.MONTH)

        val currentYear =
            calendar.get(Calendar.YEAR)

        // NEXT BUTTON

        val isCurrentMonth =
            BudgetState.selectedMonth == currentMonth &&
                    BudgetState.selectedYear == currentYear

        binding.btnNextMonth.isEnabled =
            !isCurrentMonth

        binding.btnNextMonth.alpha =
            if (isCurrentMonth) 0.3f else 1f

        // PREVIOUS BUTTON

        val hasPreviousBudgetMonth =
            BudgetState.availableMonths.any {

                val budgetMonth = it.month - 1
                val budgetYear = it.year

                budgetYear < BudgetState.selectedYear ||

                        (
                                budgetYear == BudgetState.selectedYear &&
                                        budgetMonth < BudgetState.selectedMonth
                                )
            }

        binding.btnPrevMonth.isEnabled =
            hasPreviousBudgetMonth

        binding.btnPrevMonth.alpha =
            if (hasPreviousBudgetMonth) 1f else 0.3f
    }
    private fun updateMonthText() {

        binding.tvSelectedMonth.text =
            MonthUtils.formatMonthYear(
                BudgetState.selectedMonth,
                BudgetState.selectedYear
            )

        updateAddBudgetButtonState()
    }
    private fun loadAvailableMonths() {

        RetrofitClient.instance
            .getAvailableBudgetMonths()
            .enqueue(object :
                retrofit2.Callback<AvailableMonthsResponse> {

                override fun onResponse(
                    call: retrofit2.Call<AvailableMonthsResponse>,
                    response: retrofit2.Response<AvailableMonthsResponse>
                ) {

                    BudgetState.availableMonths =
                        response.body()?.data ?: emptyList()

                    updateMonthText()
                    updateMonthButtons()
                    loadBudgetsByMonth()
                }

                override fun onFailure(
                    call: retrofit2.Call<AvailableMonthsResponse>,
                    t: Throwable
                ) {

                }
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
