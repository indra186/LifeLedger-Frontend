package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.TransactionsAdapter
import com.example.untitled.databinding.FragmentTransactionsBinding
import com.example.untitled.viewmodels.TransactionsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionsAdapter: TransactionsAdapter
    
    private lateinit var viewModel: TransactionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[TransactionsViewModel::class.java]
        
        binding.fabAddTransaction.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.action_transactionsFragment_to_addTransactionFragment)
        }
        
        binding.ivBack.setOnClickListener {
            if (isAdded) findNavController().navigateUp()
        }
        
        // RecyclerView Setup
        transactionsAdapter = TransactionsAdapter(emptyList())
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = transactionsAdapter
        
        // Filter Setup - Using proper IDs or just removing the fragile logic
        setupFilters()
        
        observeTransactions()
    }
    
    private fun setupFilters() {
        // Since the layout doesn't have IDs for individual filter buttons yet, 
        // and getChildAt is fragile, we should ideally add IDs to the layout.
        // For now, I will add a safer check if we were to use getChildAt, 
        // but it's better to just use the binding if we had IDs.
        
        // Let's assume we might add IDs or just keep it simple for now to avoid crashes.
        // I will modify the XML to add IDs for these filters in the next step.
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collectLatest { transactions ->
                if (_binding == null) return@collectLatest
                
                binding.pbLoading.visibility = View.GONE
                if (transactions.isEmpty()) {
                    binding.rvTransactions.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.VISIBLE
                } else {
                    binding.rvTransactions.visibility = View.VISIBLE
                    binding.layoutEmptyState.visibility = View.GONE
                    transactionsAdapter.updateTransactions(transactions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
