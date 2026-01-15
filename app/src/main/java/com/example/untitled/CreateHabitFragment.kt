package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentCreateHabitBinding
import com.example.untitled.viewmodels.CreateHabitViewModel
import com.example.untitled.viewmodels.UIState

class CreateHabitFragment : Fragment() {
    private var _binding: FragmentCreateHabitBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateHabitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[CreateHabitViewModel::class.java]
        
        setupObservers()

        binding.btnCreateHabit.setOnClickListener {
            val title = binding.etHabitName.text.toString().trim()
            // Default frequency if not present in UI
            val frequency = "daily" 

            viewModel.saveHabit(title, frequency)
        }
    }
    
    private fun setupObservers() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UIState.Loading -> {
                    binding.btnCreateHabit.isEnabled = false
                }
                is UIState.Success -> {
                    binding.btnCreateHabit.isEnabled = true
                    Toast.makeText(context, "Habit created!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
                    binding.btnCreateHabit.isEnabled = true
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
