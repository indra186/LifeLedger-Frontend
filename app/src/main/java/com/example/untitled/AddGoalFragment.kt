package com.example.untitled

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentAddGoalBinding
import java.util.Calendar

class AddGoalFragment : Fragment() {

    private var _binding: FragmentAddGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button navigation
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Date Picker for Target Date
        binding.tvTargetDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Month is 0-indexed, so add 1
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.tvTargetDate.text = formattedDate
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Save Goal Button
        binding.btnSaveGoal.setOnClickListener {
            // Logic to save the goal would go here (validation, database insert, etc.)
            
            val goalName = binding.etGoalName.text.toString()
            if (goalName.isBlank()) {
                Toast.makeText(context, "Please enter a goal name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(context, "Goal added: $goalName", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
