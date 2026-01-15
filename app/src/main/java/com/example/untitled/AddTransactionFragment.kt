package com.example.untitled

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentAddTransactionNewBinding
import com.example.untitled.viewmodels.AddTransactionViewModel
import com.example.untitled.viewmodels.UIState
import java.util.Calendar

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionNewBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AddTransactionViewModel
    private var selectedType: String = "expense"
    private var selectedCategory: String = "Food" // Default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionNewBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[AddTransactionViewModel::class.java]
        
        setupDateAndTimePickers()
        setupTypeSwitcher()
        setupCategoryGrid()
        setupObservers()
        
        binding.ivBack.setOnClickListener {
             if (isAdded) findNavController().navigateUp()
        }
        
        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString().trim()
            val desc = binding.etDesc.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            
            // Title logic: Use description or Category name
            val title = if (desc.isNotEmpty()) desc else selectedCategory
            
            viewModel.saveTransaction(title, amount, selectedType, selectedCategory, date, desc)
        }
    }
    
    private fun setupObservers() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UIState.Loading -> {
                    binding.btnSave.isEnabled = false
                }
                is UIState.Success -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(context, "Transaction added", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupDateAndTimePickers() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                 val selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                 binding.etDate.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTypeSwitcher() {
        // Default state: Expense selected
        selectedType = "expense"
        setExpenseSelected()

        binding.btnExpense.setOnClickListener {
            if (selectedType != "expense") {
                selectedType = "expense"
                setExpenseSelected()
            }
        }

        binding.btnIncome.setOnClickListener {
            if (selectedType != "income") {
                selectedType = "income"
                setIncomeSelected()
            }
        }
    }
    private fun setExpenseSelected() {
        binding.btnExpense.backgroundTintList =
            requireContext().getColorStateList(R.color.expense_red)
        binding.btnExpense.setTextColor(requireContext().getColor(R.color.white))

        binding.btnIncome.backgroundTintList =
            requireContext().getColorStateList(R.color.toggle_unselected)
        binding.btnIncome.setTextColor(requireContext().getColor(R.color.black))
    }

    private fun setIncomeSelected() {
        binding.btnIncome.backgroundTintList =
            requireContext().getColorStateList(R.color.expense_red)
        binding.btnIncome.setTextColor(requireContext().getColor(R.color.white))

        binding.btnExpense.backgroundTintList =
            requireContext().getColorStateList(R.color.toggle_unselected)
        binding.btnExpense.setTextColor(requireContext().getColor(R.color.black))
    }


    private fun setupCategoryGrid() {
        val clickListener = View.OnClickListener { v ->
            if (v is TextView) {
                selectedCategory = v.text.toString()
                Toast.makeText(context, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }
        }
        
        val grid = binding.categoryGrid
        for (i in 0 until grid.childCount) {
             val view = grid.getChildAt(i)
             if (view is android.widget.LinearLayout) {
                 for (j in 0 until view.childCount) {
                     view.getChildAt(j).setOnClickListener(clickListener)
                 }
             }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
