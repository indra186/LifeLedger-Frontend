package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentPremiumBinding

class PremiumFragment : Fragment() {
    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Allow clicking either the pricing card or the hero card to go to payment
        binding.cardPricing.setOnClickListener {
            findNavController().navigate(R.id.action_premiumFragment_to_paymentFragment)
        }
        
        binding.cardHero.setOnClickListener {
             findNavController().navigate(R.id.action_premiumFragment_to_paymentFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
