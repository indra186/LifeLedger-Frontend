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
import com.example.untitled.adapters.AccountsAdapter
import com.example.untitled.databinding.FragmentAccountsBinding
import com.example.untitled.viewmodels.AccountsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AccountsViewModel
    private lateinit var adapter: AccountsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[AccountsViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        viewModel.loadAccounts()
        binding.btnAddNew.setOnClickListener {
             findNavController().navigate(R.id.action_accountsFragment_to_addAccountFragment)
        }
        
        binding.ivBack.setOnClickListener {
             findNavController().popBackStack()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = AccountsAdapter(emptyList())
        binding.rvAccounts.layoutManager = LinearLayoutManager(context)
        binding.rvAccounts.adapter = adapter
    }
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.accounts.collectLatest { accounts ->

                if (accounts.isEmpty()) {
                    binding.rvAccounts.visibility = View.GONE
                    binding.tvEmptyAccounts.visibility = View.VISIBLE
                } else {
                    binding.rvAccounts.visibility = View.VISIBLE
                    binding.tvEmptyAccounts.visibility = View.GONE
                    adapter.updateAccounts(accounts)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
