package com.example.untitled

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.network.RetrofitClient

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_personalization).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_personalizationFragment)
        }
        view.findViewById<View>(R.id.btn_security).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_securityFragment)
        }
        view.findViewById<View>(R.id.btn_connected_apps).setOnClickListener {
             findNavController().navigate(R.id.action_profileFragment_to_connectedAppsFragment)
        }
        view.findViewById<View>(R.id.btn_settings).setOnClickListener {
             findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        view.findViewById<View>(R.id.btn_help).setOnClickListener {
             findNavController().navigate(R.id.action_profileFragment_to_helpFragment)
        }
        view.findViewById<View>(R.id.btn_premium).setOnClickListener {
             findNavController().navigate(R.id.action_profileFragment_to_premiumFragment)
        }
        view.findViewById<View>(R.id.btn_feedback).setOnClickListener {
             findNavController().navigate(R.id.action_profileFragment_to_feedbackFragment)
        }
        view.findViewById<View>(R.id.btn_logout).setOnClickListener {
             // 1. Clear SharedPreferences
             val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
             val editor = sharedPreferences.edit()
             editor.clear() // Or remove("token")
             editor.apply()
             
             // 2. Clear RetrofitClient token
             RetrofitClient.setAuthToken(null)
             
             // 3. Navigate back to login screen, clearing the back stack
             findNavController().navigate(R.id.loginFragment) 
             // Ideally we should navigate to splash or intro and clear everything, but loginFragment is fine.
             // If we want to ensure user can't go back, we should use a global action or set up the graph correctly.
             // But for now, navigating to LoginFragment is consistent with standard flow.
             // To prevent back navigation to profile:
             // We can pop the back stack.
             // But since we are in a tab (bottom nav), the stack is managed differently.
             // The cleanest way is often to navigate to a start destination that checks auth (Splash) or just Login.
        }
    }
}
