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

    private var isPlanSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Pricing card selection
        binding.cardPricing.setOnClickListener {

            // 1️⃣ Mark selected (border appears)
            binding.cardPricing.isSelected = true

            // 2️⃣ Let UI render selection (VERY IMPORTANT)
            binding.cardPricing.postDelayed({

                val bundle = Bundle().apply {
                    putInt("amount", 99)
                    putString("plan", "Annual")
                }

                findNavController().navigate(
                    R.id.action_premiumFragment_to_paymentFragment,
                    bundle
                )

            }, 120) // 100–150ms is ideal
        }

    }

    private fun navigateToPayment() {
        val bundle = Bundle().apply {
            putInt("amount", 99)
            putString("plan", "Annual")
        }

        findNavController().navigate(
            R.id.action_premiumFragment_to_paymentFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
