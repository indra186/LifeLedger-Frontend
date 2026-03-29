package com.example.untitled

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.untitled.network.RetrofitClient

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        android.util.Log.d("PROFILE_FRAGMENT", "ProfileFragment loaded")

        view.findViewById<View>(R.id.btn_premium).setOnClickListener {
            android.util.Log.d("PROFILE_CLICK", "Premium clicked")
            findNavController().navigate(R.id.premiumFragment)
        }

        // Logout
        view.findViewById<View>(R.id.btn_logout).setOnClickListener {
            val prefs = requireActivity()
                .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            RetrofitClient.setAuthToken(null)

            findNavController().navigate(R.id.loginFragment)
        }
    }
}
