package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.TransactionsAdapter
import com.example.untitled.databinding.FragmentTransactionsBinding
import com.example.untitled.viewmodels.TransactionsApiViewModel
import androidx.navigation.fragment.findNavController
import com.example.untitled.models.TransactionsResponse
import com.example.untitled.network.RetrofitClient
import com.example.untitled.utils.MonthUtils
import java.util.Calendar
import com.example.untitled.utils.FinanceState


class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransactionsAdapter
    private lateinit var viewModel: TransactionsApiViewModel
    private var allTransactions = listOf<com.example.untitled.models.TransactionItem>()

    private var currentFilter = "all"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TransactionsApiViewModel::class.java]

        // ✅ BACK BUTTON
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // ✅ ADD TRANSACTION FAB
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(
                R.id.action_transactionsFragment_to_addTransactionFragment
            )
        }

        // ✅ Adapter with navigation to detail page
        adapter = TransactionsAdapter(emptyList()) { transaction ->

            android.util.Log.d(
                "TX_CLICK",
                "Transactions click tx_id=${transaction.id}"
            )

            val bundle = Bundle().apply {
                putInt("tx_id", transaction.id)
            }

            findNavController().navigate(
                R.id.action_transactionsFragment_to_transactionDetailFragment,
                bundle
            )
        }

        binding.rvTransactions.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvTransactions.adapter = adapter

        // Observe data
        viewModel.transactions.observe(viewLifecycleOwner) {
            adapter.updateApiTransactions(it)
        }
        setupMonthSelector()
        updateMonthButtons()
        setupFilters()
        loadTransactionsByMonth()
    }
    private fun loadTransactionsByMonth() {

        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)

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

                allTransactions = list

                applyFilter()
                updateMonthButtons()
            }

            override fun onFailure(
                call: retrofit2.Call<TransactionsResponse>,
                t: Throwable
            ) {

            }
        })
    }
    private fun applyFilter() {

        val filteredList = when(currentFilter) {

            "income" -> {
                allTransactions.filter {
                    it.type.lowercase() == "income"
                }
            }

            "expense" -> {
                allTransactions.filter {
                    it.type.lowercase() == "expense"
                }
            }

            else -> {
                allTransactions
            }
        }

        adapter.updateApiTransactions(filteredList)

        if (filteredList.isEmpty()) {

            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvTransactions.visibility = View.GONE

        } else {

            binding.layoutEmptyState.visibility = View.GONE
            binding.rvTransactions.visibility = View.VISIBLE
        }
    }
    private fun setupMonthSelector() {

        updateMonthText()

        binding.btnPrevMonth.setOnClickListener {
            if (!binding.btnPrevMonth.isEnabled)
                return@setOnClickListener

            FinanceState.selectedMonth--

            if (FinanceState.selectedMonth < 0) {
                FinanceState.selectedMonth = 11
                FinanceState.selectedYear--
            }
            updateMonthButtons()
            updateMonthText()
            loadTransactionsByMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            if (!binding.btnNextMonth.isEnabled)
                return@setOnClickListener


            FinanceState.selectedMonth++

            if (FinanceState.selectedMonth > 11) {
                FinanceState.selectedMonth = 0
                FinanceState.selectedYear++
            }
            updateMonthButtons()
            updateMonthText()
            loadTransactionsByMonth()
        }
    }
    private fun updateMonthText() {

        binding.tvSelectedMonth.text =
            MonthUtils.formatMonthYear(
                FinanceState.selectedMonth,
                FinanceState.selectedYear
            )
    }
    private fun setupFilters() {

        binding.tvFilterIncome.setOnClickListener {

            currentFilter = "income"

            applyFilter()

            updateFilterUI()
        }

        binding.tvFilterExpense.setOnClickListener {

            currentFilter = "expense"

            applyFilter()

            updateFilterUI()
        }

        binding.tvFilterAll.setOnClickListener {

            currentFilter = "all"

            applyFilter()

            updateFilterUI()
        }
    }
    private fun updateFilterUI() {

        binding.tvFilterIncome.alpha =
            if (currentFilter == "income") 1f else 0.5f

        binding.tvFilterExpense.alpha =
            if (currentFilter == "expense") 1f else 0.5f

        binding.tvFilterAll.alpha =
            if (currentFilter == "all") 1f else 0.5f
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
            FinanceState.selectedMonth ==
                    FinanceState.firstTransactionMonth &&
                    FinanceState.selectedYear ==
                    FinanceState.firstTransactionYear

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
