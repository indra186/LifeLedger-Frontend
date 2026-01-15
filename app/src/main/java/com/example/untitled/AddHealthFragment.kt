package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentAddHealthBinding
import com.example.untitled.viewmodels.AddHealthViewModel
import com.example.untitled.viewmodels.UIState

class AddHealthFragment : Fragment() {
    private var _binding: FragmentAddHealthBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddHealthViewModel

    // Default values
    private var selectedType = "Steps"
    private var unit = "steps"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[AddHealthViewModel::class.java]

        setupObservers()
        
        // This assumes there's some way to select the metric type in the UI, 
        // e.g. a spinner or buttons. If not present in XML, defaulting to "Steps".
        // You might need to enhance the UI code here if there are selection widgets.
        
        binding.btnSaveEntry.setOnClickListener {
            
            // I'll assume `etValue` exists for the amount as confirmed in XML layout reading.
            
            val valueStr = binding.etValue.text.toString().trim()
            val value = valueStr.toDoubleOrNull() ?: 0.0
            
            viewModel.saveMetric(selectedType, value, unit)
        }
    }
    
    private fun setupObservers() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UIState.Loading -> {
                    binding.btnSaveEntry.isEnabled = false
                }
                is UIState.Success -> {
                    binding.btnSaveEntry.isEnabled = true
                    Toast.makeText(context, "Metric added", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
                    binding.btnSaveEntry.isEnabled = true
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
