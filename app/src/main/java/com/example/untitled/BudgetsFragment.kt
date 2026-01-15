package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.BudgetsAdapter
import com.example.untitled.databinding.FragmentBudgetsBinding
import com.example.untitled.viewmodels.BudgetsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.util.Log


class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetsAdapter: BudgetsAdapter
    private lateinit var viewModel: BudgetsViewModel

    private lateinit var pbLoading: ProgressBar
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("BUDGET_UI", "BudgetsFragment loaded")

        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BudgetsViewModel::class.java]

        pbLoading = binding.pbLoading
        layoutEmptyState = binding.layoutEmptyState

        budgetsAdapter = BudgetsAdapter(emptyList())
        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = budgetsAdapter

        binding.tvAddNew.setOnClickListener {
            findNavController().navigate(R.id.action_budgetsFragment_to_createBudgetFragment)
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        observeBudgets()
        viewModel.loadBudgets()
    }

    private fun observeBudgets() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.budgets.collectLatest { budgets ->
                pbLoading.visibility = View.GONE

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
                    binding.tvRemainingVal.text = "₹${totalBudget - totalSpent} left"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
