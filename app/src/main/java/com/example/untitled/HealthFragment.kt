package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.HealthAdapter
import com.example.untitled.databinding.FragmentHealthBinding
import com.example.untitled.viewmodels.HealthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HealthFragment : Fragment() {

    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HealthViewModel
    private lateinit var healthAdapter: HealthAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        setupRecyclerView()
        setupObservers()

        binding.tvAddEntry.setOnClickListener {
             findNavController().navigate(R.id.action_healthFragment_to_addHealthFragment)
        }
    }
    
    private fun setupRecyclerView() {
        healthAdapter = HealthAdapter(emptyList())
        binding.rvHealthMetrics.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = healthAdapter
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.metrics.collectLatest { metrics ->
                healthAdapter.updateMetrics(metrics)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
