package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

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
             // Navigate back to login screen, clearing the back stack so user can't go back
             findNavController().navigate(R.id.loginFragment) 
             // Ideally you would clear the stack here, e.g. popUpTo(R.id.nav_graph) { inclusive = true }
        }
    }
}
