package com.example.untitled

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentSplashBinding
import com.example.untitled.network.RetrofitClient

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load animations
        val logoAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_logo)
        val textAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_text_fade_in)

        // Apply animations
        binding.logoContainer.startAnimation(logoAnim)
        binding.appTitle.startAnimation(textAnim)
        binding.appSubtitle.startAnimation(textAnim)

        // Delay for 2.5 seconds (to allow animations to finish) then navigate based on login status
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if fragment is still attached to avoid crashes if user exits quickly
            if (isAdded) {
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)

                if (token.isNullOrEmpty()) {
                    findNavController().navigate(R.id.action_splashFragment_to_introFragment)
                } else {
                    // Set the token for API requests
                    RetrofitClient.setAuthToken(token)
                    findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
                }
            }
        }, 2500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
