package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentFinanceBinding

class FinanceFragment : Fragment() {

    private var _binding: FragmentFinanceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvViewAllTransactions.setOnClickListener {
             findNavController().navigate(R.id.action_financeFragment_to_transactionsFragment)
        }
        
        // Setup navigation for the new buttons
        binding.btnAccounts.setOnClickListener {
             findNavController().navigate(R.id.action_financeFragment_to_accountsFragment)
        }

        binding.btnBudgets.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_budgetsFragment)
        }

        binding.btnReports.setOnClickListener {
            findNavController().navigate(R.id.action_financeFragment_to_insightsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
