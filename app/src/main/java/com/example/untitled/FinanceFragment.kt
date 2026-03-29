package com.example.untitled

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.TransactionsAdapter
import com.example.untitled.databinding.FragmentFinanceBinding
import com.example.untitled.models.TransactionsResponse
import com.example.untitled.network.RetrofitClient
import androidx.navigation.fragment.findNavController


class FinanceFragment : Fragment() {

    private var _binding: FragmentFinanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 1. NORMAL BUTTON NAVIGATION (USE findNavController)

        binding.btnAccounts.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_accountsFragment
            )
        }

        binding.btnBudgets.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_budgetsFragment
            )
        }

        binding.btnReports.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_insightsFragment
            )
        }

        binding.tvViewAllTransactions.setOnClickListener {
            findNavController().navigate(
                R.id.action_financeFragment_to_transactionsFragment
            )
        }

        // 🔹 2. RECYCLERVIEW / ADAPTER NAVIGATION (USE NavHostFragment)

        adapter = TransactionsAdapter(emptyList()) { transaction ->

            android.util.Log.d("TX_CLICK", "Finance click tx_id=${transaction.id}")

            val bundle = Bundle().apply {
                putInt("tx_id", transaction.id)
            }

            val navController =
                (requireActivity().supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                    .navController

            navController.navigate(
                R.id.action_global_transactionDetailFragment,
                bundle
            )
        }

        binding.rvLatestTransactions.layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvLatestTransactions.adapter = adapter

        loadLatestTransactions()
    }

    private fun loadLatestTransactions() {
        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getString("userId", null) ?: return

        RetrofitClient.instance.getTransactions(userId)
            .enqueue(object : retrofit2.Callback<TransactionsResponse> {
                override fun onResponse(
                    call: retrofit2.Call<TransactionsResponse>,
                    response: retrofit2.Response<TransactionsResponse>
                ) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.updateApiTransactions(list.take(4))
                }

                override fun onFailure(
                    call: retrofit2.Call<TransactionsResponse>,
                    t: Throwable
                ) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
