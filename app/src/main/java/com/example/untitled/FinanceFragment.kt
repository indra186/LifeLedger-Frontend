package com.example.untitled

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.TransactionsAdapter
import com.example.untitled.databinding.FragmentFinanceBinding
import com.example.untitled.models.TransactionsResponse
import com.example.untitled.network.RetrofitClient
import androidx.navigation.fragment.findNavController
import com.example.untitled.utils.MonthUtils
import java.util.Calendar
import com.example.untitled.utils.FinanceState
import com.example.untitled.models.DashboardResponse


class FinanceFragment : Fragment() {

    private var _binding: FragmentFinanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 1. NORMAL BUTTON NAVIGATION (USE findNavController)

        binding.btnAccounts.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_accountsFragment
            )
        }

        binding.btnBudgets.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_budgetsFragment
            )
        }

        binding.btnReports.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_insightsFragment
            )
        }

        binding.tvViewAllTransactions.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_transactionsFragment
            )
        }

        // 🔹 2. RECYCLERVIEW / ADAPTER NAVIGATION (USE NavHostFragment)

        adapter = TransactionsAdapter(emptyList()) { transaction ->

            android.util.Log.d("TX_CLICK", "Finance click tx_id=${transaction.id}")

            val bundle = Bundle().apply {
                putInt("tx_id", transaction.id)
            }

            val navController =
                (requireActivity().supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                    .navController

            navController.navigate(
                R.id.action_global_transactionDetailFragment,
                bundle
            )
        }

        binding.rvLatestTransactions.layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvLatestTransactions.adapter = adapter

        setupMonthSelector()
        updateMonthButtons()
        loadLatestTransactions()
        loadFinanceSummary()
        loadDashboardBalance()
    }
    private fun loadDashboardBalance() {

        RetrofitClient.instance.getDashboardData()
            .enqueue(object : retrofit2.Callback<DashboardResponse> {

                override fun onResponse(
                    call: retrofit2.Call<DashboardResponse>,
                    response: retrofit2.Response<DashboardResponse>
                ) {

                    if (!isAdded || _binding == null) return

                    if (response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        val data = response.body()!!.data

                        binding.tvBalance.text =
                            "₹${data.total_balance}"
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<DashboardResponse>,
                    t: Throwable
                ) {

                }
            })
    }
    private fun loadLatestTransactions() {

        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getString("userId", null) ?: return

        val (startDate, endDate) =
            MonthUtils.getMonthDateRange(
                FinanceState.selectedMonth,
                FinanceState.selectedYear
            )

        RetrofitClient.instance.getTransactionsByMonth(
            userId,
            startDate,
            endDate
        ).enqueue(object : retrofit2.Callback<TransactionsResponse> {

            override fun onResponse(
                call: retrofit2.Call<TransactionsResponse>,
                response: retrofit2.Response<TransactionsResponse>
            ) {

                val list = response.body()?.data ?: emptyList()
                if (list.isNotEmpty()) {

                    val oldestTransaction =
                        list.minByOrNull { it.tx_date }

                    oldestTransaction?.let {

                        val parts = it.tx_date.split("-")

                        FinanceState.firstTransactionYear =
                            parts[0].toInt()

                        FinanceState.firstTransactionMonth =
                            parts[1].toInt() - 1
                    }
                }

                adapter.updateApiTransactions(list.take(4))
                updateMonthButtons()
            }

            override fun onFailure(
                call: retrofit2.Call<TransactionsResponse>,
                t: Throwable
            ) {

            }
        })
    }
    private fun loadFinanceSummary() {

        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getString("userId", null) ?: return

        val (startDate, endDate) =
            MonthUtils.getMonthDateRange(
                FinanceState.selectedMonth,
                FinanceState.selectedYear
            )

        RetrofitClient.instance.getTransactionsByMonth(
            userId,
            startDate,
            endDate
        ).enqueue(object : retrofit2.Callback<TransactionsResponse> {

            override fun onResponse(
                call: retrofit2.Call<TransactionsResponse>,
                response: retrofit2.Response<TransactionsResponse>
            ) {

                val list = response.body()?.data ?: emptyList()

                var income = 0.0
                var expense = 0.0

                list.forEach {

                    if (it.type.lowercase() == "income") {
                        income += it.amount
                    } else {
                        expense += it.amount
                    }
                }

                val net = income - expense

                // You'll create IDs below
                binding.tvIncomeValue.text =
                    "₹%.2f".format(income)

                binding.tvExpenseValue.text =
                    "₹%.2f".format(expense)
                binding.tvNetValue.text =
                    "₹%.2f".format(net)
            }

            override fun onFailure(
                call: retrofit2.Call<TransactionsResponse>,
                t: Throwable
            ) {

            }
        })
    }
    private fun setupMonthSelector() {

        updateMonthText()

        binding.btnPrevMonth.setOnClickListener {
            if (!binding.btnPrevMonth.isEnabled) return@setOnClickListener

            FinanceState.selectedMonth--

            if (FinanceState.selectedMonth < 0) {
                FinanceState.selectedMonth = 11
                FinanceState.selectedYear--
            }

            updateMonthText()
            loadLatestTransactions()
            loadFinanceSummary()
        }

        binding.btnNextMonth.setOnClickListener {
            if (!binding.btnNextMonth.isEnabled)
                return@setOnClickListener

            FinanceState.selectedMonth++

            if (FinanceState.selectedMonth > 11) {
                FinanceState.selectedMonth = 0
                FinanceState.selectedYear++
            }

            updateMonthText()
            loadLatestTransactions()
            loadFinanceSummary()
        }
    }
    private fun updateMonthText() {

        binding.tvSelectedMonth.text =
            MonthUtils.formatMonthYear(
                FinanceState.selectedMonth,
                FinanceState.selectedYear
            )
        updateMonthButtons()
    }
    private fun updateMonthButtons() {

        val currentCalendar = Calendar.getInstance()

        val currentMonth =
            currentCalendar.get(Calendar.MONTH)

        val currentYear =
            currentCalendar.get(Calendar.YEAR)

        // NEXT BUTTON

        val isCurrentMonth =
            FinanceState.selectedMonth == currentMonth &&
                    FinanceState.selectedYear == currentYear

        binding.btnNextMonth.isEnabled =
            !isCurrentMonth

        binding.btnNextMonth.alpha =
            if (isCurrentMonth) 0.3f else 1f

        // PREVIOUS BUTTON

        val isFirstMonth =
            FinanceState.selectedMonth == FinanceState.firstTransactionMonth &&
                    FinanceState.selectedYear == FinanceState.firstTransactionYear

        binding.btnPrevMonth.isEnabled =
            !isFirstMonth

        binding.btnPrevMonth.alpha =
            if (isFirstMonth) 0.3f else 1f
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
