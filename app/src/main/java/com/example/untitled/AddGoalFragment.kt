package com.example.untitled

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.viewmodels.AddGoalViewModel
import com.example.untitled.viewmodels.UIState
import java.util.Calendar

class AddGoalFragment : Fragment() {

    private lateinit var viewModel: AddGoalViewModel
    private lateinit var etGoalName: EditText
    private lateinit var etTargetAmount: EditText
    private lateinit var tvTargetDate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AddGoalViewModel::class.java]

        etGoalName = view.findViewById(R.id.et_goal_name)
        etTargetAmount = view.findViewById(R.id.et_target_amount)
        tvTargetDate = view.findViewById(R.id.tv_target_date)

        setupObservers(view)

        // Back button navigation
        view.findViewById<View>(R.id.btn_back).setOnClickListener {
            if (isAdded) {
                findNavController().navigateUp()
            }
        }

        // Date Picker for Target Date
        tvTargetDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    tvTargetDate.text = formattedDate
                },
                year,
                month,
                day
            ).show()
        }

        // Save Goal Button
        view.findViewById<View>(R.id.btn_save_goal).setOnClickListener {
            val goalName = etGoalName.text.toString().trim()
            val amountStr = etTargetAmount.text.toString().trim()
            val date = tvTargetDate.text.toString()

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            
            viewModel.saveGoal(goalName, amount, date)
        }
    }

    private fun setupObservers(view: View) {
        val btnSave = view.findViewById<View>(R.id.btn_save_goal)
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    btnSave.isEnabled = false
                }
                is UIState.Success -> {
                    btnSave.isEnabled = true
                    Toast.makeText(context, "Goal added successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
                    btnSave.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
