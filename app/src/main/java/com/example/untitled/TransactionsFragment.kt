package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.TransactionsAdapter
import com.example.untitled.databinding.FragmentTransactionsBinding
import com.example.untitled.viewmodels.TransactionsApiViewModel
import androidx.navigation.fragment.findNavController


class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransactionsAdapter
    private lateinit var viewModel: TransactionsApiViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel = ViewModelProvider(this)[TransactionsApiViewModel::class.java]
//
//        // ✅ Adapter with GLOBAL navigation
//        adapter = TransactionsAdapter(emptyList()) { transaction ->
//
//            android.util.Log.d(
//                "TX_CLICK",
//                "Transactions click tx_id=${transaction.id}"
//            )
//
//            val bundle = Bundle().apply {
//                putInt("tx_id", transaction.id)
//            }
//
//            findNavController().navigate(
//                R.id.action_transactionsFragment_to_transactionDetailFragment,
//                bundle
//            )
//        }
//
//
//        binding.rvTransactions.layoutManager =
//            LinearLayoutManager(requireContext())
//
//        binding.rvTransactions.adapter = adapter
//
//        // Observe data
//        viewModel.transactions.observe(viewLifecycleOwner) {
//            adapter.updateApiTransactions(it)
//        }
//
//        viewModel.loadTransactions()
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TransactionsApiViewModel::class.java]

        // ✅ BACK BUTTON
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // ✅ ADD TRANSACTION FAB
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(
                R.id.action_transactionsFragment_to_addTransactionFragment
            )
        }

        // ✅ Adapter with navigation to detail page
        adapter = TransactionsAdapter(emptyList()) { transaction ->

            android.util.Log.d(
                "TX_CLICK",
                "Transactions click tx_id=${transaction.id}"
            )

            val bundle = Bundle().apply {
                putInt("tx_id", transaction.id)
            }

            findNavController().navigate(
                R.id.action_transactionsFragment_to_transactionDetailFragment,
                bundle
            )
        }

        binding.rvTransactions.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvTransactions.adapter = adapter

        // Observe data
        viewModel.transactions.observe(viewLifecycleOwner) {
            adapter.updateApiTransactions(it)
        }

        viewModel.loadTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
