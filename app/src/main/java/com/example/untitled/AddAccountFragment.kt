package com.example.untitled

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentAddAccountBinding
import com.example.untitled.viewmodels.AddAccountViewModel
import com.example.untitled.viewmodels.UIState

class AddAccountFragment : Fragment() {

    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AddAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[AddAccountViewModel::class.java]
        setupObservers()
        
        binding.ivBack.setOnClickListener {
             findNavController().popBackStack()
        }
        
        binding.cardBank.setOnClickListener { showAddAccountDialog("Bank") }
        binding.cardCredit.setOnClickListener { showAddAccountDialog("Credit") }
        binding.cardCash.setOnClickListener { showAddAccountDialog("Cash") }
    }
    
    private fun showAddAccountDialog(type: String) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val etName = EditText(context).apply {
            hint = "Account Name (e.g. Chase Checking)"
        }
        val etBalance = EditText(context).apply {
            hint = "Initial Balance"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        
        layout.addView(etName)
        layout.addView(etBalance)
        
        AlertDialog.Builder(context)
            .setTitle("Add $type Account")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val balance = etBalance.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.saveAccount(name, type, balance)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupObservers() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UIState.Loading -> {
                    // distinct loading state not easily shown in dialog flow, 
                    // but we could block UI. For now, we trust the quick DB op.
                }
                is UIState.Success -> {
                    Toast.makeText(context, "Account added successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is UIState.Error -> {
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
