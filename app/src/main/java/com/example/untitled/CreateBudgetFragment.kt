package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentCreateBudgetBinding
import com.example.untitled.viewmodels.AddBudgetViewModel
import com.example.untitled.viewmodels.UIState

class CreateBudgetFragment : Fragment() {

    private var _binding: FragmentCreateBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddBudgetViewModel
    private var selectedCategory = "Food & Dining"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AddBudgetViewModel::class.java]


        setupCategorySelection()
        setupObservers()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreateBudget.setOnClickListener {
            val amount = binding.etLimit.text.toString().toDoubleOrNull() ?: 0.0
            viewModel.saveBudget(selectedCategory, amount)
        }
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

    private fun setupCategorySelection() {
        val clickListener = View.OnClickListener { v ->
            if (v is TextView) {
                selectedCategory = v.text.toString()
                Toast.makeText(context, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }
        }

        val grid = binding.categoryGrid
        for (i in 0 until grid.childCount) {
            val row = grid.getChildAt(i) as? android.widget.LinearLayout
            row?.let {
                for (j in 0 until it.childCount) {
                    it.getChildAt(j).setOnClickListener(clickListener)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
