package com.example.lifeledger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.databinding.FragmentCreateBudgetBinding
import com.example.lifeledger.utils.FinanceState
import com.example.lifeledger.viewmodels.AddBudgetViewModel
import com.example.lifeledger.viewmodels.UIState

class CreateBudgetFragment : Fragment() {

    private var _binding: FragmentCreateBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddBudgetViewModel
    private var selectedCategory = "Food"
    private val categories = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Entertainment",
        "Healthcare",
        "Education",
        "Housing",
        "Others"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AddBudgetViewModel::class.java]

        setupObservers()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.btnCreateBudget.setOnClickListener {
            val amount = binding.etLimit.text.toString().toDoubleOrNull() ?: 0.0
            val alertEnabled = binding.cbAlert.isChecked
            viewModel.saveBudget(
                selectedCategory,
                amount,
                alertEnabled,
                FinanceState.selectedMonth + 1,
                FinanceState.selectedYear
            )
        }
        binding.tvSelectedCategory.setOnClickListener {
            showCategoryPicker()
        }
    }
    private fun showCategoryPicker() {

        val builder = android.app.AlertDialog.Builder(requireContext())

        builder.setTitle("Select Category")

        builder.setItems(categories.toTypedArray()) { _, which ->

            selectedCategory = categories[which]

            binding.tvSelectedCategory.text = selectedCategory

        }

        builder.show()
    }
    private fun setupObservers() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.btnCreateBudget.isEnabled = false
                }
                is UIState.Success<*> -> {
                    binding.btnCreateBudget.isEnabled = true
                    Toast.makeText(context, "Budget created", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
                    binding.btnCreateBudget.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
