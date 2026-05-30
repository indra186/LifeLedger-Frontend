package com.example.lifeledger

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.network.RetrofitClient

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
        val prefs = requireActivity()
            .getSharedPreferences(
                "UserPrefs",
                Context.MODE_PRIVATE
            )

        val name =
            prefs.getString("name", "")

        val email =
            prefs.getString("email", "")
        Log.d("PROFILE_DEBUG", "NAME = $name")
        Log.d("PROFILE_DEBUG", "EMAIL = $email")
        view.findViewById<TextView>(R.id.tv_name).text =
            name

        view.findViewById<TextView>(R.id.tv_email).text =
            email
        view.findViewById<View>(
            R.id.btn_theme
        ).setOnClickListener {

            showThemeDialog()
        }
        view.findViewById<View>(R.id.btn_premium).setOnClickListener {
            android.util.Log.d("PROFILE_CLICK", "Premium clicked")
            findNavController().navigate(R.id.premiumFragment)
        }
        view.findViewById<View>(
            R.id.btn_change_password
        ).setOnClickListener {

            findNavController().navigate(
                R.id.changePasswordFragment
            )
        }

        view.findViewById<View>(
            R.id.btn_edit_email
        ).setOnClickListener {

            findNavController().navigate(
                R.id.editEmailFragment
            )
        }

        view.findViewById<View>(
            R.id.btn_delete_account
        ).setOnClickListener {

            showDeleteAccountDialog()
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
    private fun showThemeDialog() {

        val themes = arrayOf(
            "Light",
            "Dark",
            "System Default"
        )

        androidx.appcompat.app.AlertDialog.Builder(
            requireContext()
        )

            .setTitle("Choose Theme")

            .setItems(themes) { _, which ->

                val prefs =
                    requireContext()
                        .getSharedPreferences(
                            "ThemePrefs",
                            Context.MODE_PRIVATE
                        )

                val editor =
                    prefs.edit()

                when(which) {

                    0 -> {

                        editor.putString(
                            "theme",
                            "light"
                        )
                    }

                    1 -> {

                        editor.putString(
                            "theme",
                            "dark"
                        )
                    }

                    else -> {

                        editor.putString(
                            "theme",
                            "system"
                        )
                    }
                }

                editor.apply()

                requireActivity().recreate()
            }

            .show()
    }
    private fun showDeleteAccountDialog() {

        androidx.appcompat.app.AlertDialog.Builder(
            requireContext()
        )

            .setTitle("Delete Account")

            .setMessage(
                "This action cannot be undone."
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                findNavController().navigate(
                    R.id.deleteAccountFragment
                )
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }
}
