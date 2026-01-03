package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentAddAccountBinding

class AddAccountFragment : Fragment() {

    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.ivBack.setOnClickListener {
             findNavController().popBackStack()
        }
        
        val clickListener = View.OnClickListener {
            Toast.makeText(context, "Account Type Selected", Toast.LENGTH_SHORT).show()
            // In a real app, this would navigate to a detail form for that account type
            findNavController().popBackStack()
        }
        
        binding.cardBank.setOnClickListener(clickListener)
        binding.cardCredit.setOnClickListener(clickListener)
        binding.cardCash.setOnClickListener(clickListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
