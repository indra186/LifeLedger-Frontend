package com.example.untitled

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentAddTaskBinding
import com.example.untitled.viewmodels.AddTaskViewModel
import com.example.untitled.viewmodels.UIState
import java.util.Calendar

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AddTaskViewModel
    private var selectedCategory: String = "Personal" // Default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[AddTaskViewModel::class.java]
        
        setupDateAndTimePickers()
        setupCategorySelection()
        setupObservers()

        binding.ivBack.setOnClickListener {
            if (isAdded) {
                findNavController().navigateUp()
            }
        }

        binding.btnSaveTask.setOnClickListener {
            val title = binding.etTaskTitle.text.toString().trim()
            val date = binding.tvDate.text.toString()
            val time = binding.tvTime.text.toString()
            
            viewModel.saveTask(title, date, time, selectedCategory)
        }
    }
    
    private fun setupObservers() {
        viewModel.saveTaskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.btnSaveTask.isEnabled = false
                    binding.btnSaveTask.text = "Saving..."
                }
                is UIState.Success -> {
                    binding.btnSaveTask.isEnabled = true
                    binding.btnSaveTask.text = getString(R.string.create_task)
                    Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
                    binding.btnSaveTask.isEnabled = true
                    binding.btnSaveTask.text = getString(R.string.create_task)
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupDateAndTimePickers() {
        binding.layoutDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                 val selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
                 binding.tvDate.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        
        binding.layoutTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                binding.tvTime.text = selectedTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }
    
    private fun setupCategorySelection() {
        val workBtn = binding.btnCategoryWork
        val personalBtn = binding.btnCategoryPersonal
        val healthBtn = binding.btnCategoryHealth
        
        val buttons = listOf(workBtn, personalBtn, healthBtn)
        
        fun updateSelection(selected: Button) {
            selectedCategory = selected.text.toString()
            
            buttons.forEach { btn ->
                if (btn == selected) {
                    btn.alpha = 1.0f
                    btn.elevation = 8f
                } else {
                    btn.alpha = 0.5f
                    btn.elevation = 0f
                }
            }
        }
        
        // Initial Selection State
        updateSelection(personalBtn)

        workBtn.setOnClickListener { updateSelection(workBtn) }
        personalBtn.setOnClickListener { updateSelection(personalBtn) }
        healthBtn.setOnClickListener { updateSelection(healthBtn) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
