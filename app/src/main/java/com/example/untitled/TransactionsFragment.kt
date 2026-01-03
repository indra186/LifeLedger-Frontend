package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentTransactionsBinding

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Listener for FAB
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_transactionsFragment_to_addTransactionFragment)
        }
        
        // Filter functionality
        val filterContainer = view.findViewById<LinearLayout>(R.id.filter_container)
        
        // Income filter
        val incomeFilter = filterContainer.getChildAt(0) as TextView
        incomeFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Income filter applied", Toast.LENGTH_SHORT).show()
        }
        
        // Expense filter
        val expenseFilter = filterContainer.getChildAt(1) as TextView
        expenseFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Expense filter applied", Toast.LENGTH_SHORT).show()
        }
        
        // Sort/Filter menu
        val sortFilter = filterContainer.getChildAt(2) as TextView
        sortFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Sort options", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
