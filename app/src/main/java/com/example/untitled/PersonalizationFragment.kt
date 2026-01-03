package com.example.untitled

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.untitled.databinding.FragmentPersonalizationBinding
import com.google.android.material.card.MaterialCardView

class PersonalizationFragment : Fragment() {
    private var _binding: FragmentPersonalizationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setupThemeSelection()
        setupDisplayOptions()
    }

    private fun setupThemeSelection() {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val currentMode = sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        updateThemeSelectionUI(currentMode)

        binding.cardThemeLight.setOnClickListener {
            applyTheme(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.cardThemeDark.setOnClickListener {
            applyTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding.cardThemeAuto.setOnClickListener {
            applyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun applyTheme(mode: Int) {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("theme_mode", mode)
            apply()
        }

        updateThemeSelectionUI(mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun updateThemeSelectionUI(mode: Int) {
        val density = resources.displayMetrics.density
        fun dpToPx(dp: Int) = (dp * density).toInt()

        fun updateCard(card: MaterialCardView, isSelected: Boolean) {
            val context = requireContext()
            // Use color resources for dark mode support
            
            // For unselected, use @color/white which maps to dark in dark mode
            val unselectedBg = ContextCompat.getColor(context, R.color.white)
            
            // For strokes
            val unselectedStroke = if (isDarkTheme()) android.graphics.Color.DKGRAY else android.graphics.Color.parseColor("#EEEEEE")

            if (isSelected) {
                if (isDarkTheme()) {
                    card.setCardBackgroundColor(android.graphics.Color.parseColor("#152436")) // Dark blue
                    card.strokeColor = android.graphics.Color.parseColor("#64B5F6") // Lighter blue stroke
                } else {
                    card.setCardBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"))
                    card.strokeColor = android.graphics.Color.parseColor("#2196F3")
                }
                card.strokeWidth = dpToPx(2)
            } else {
                card.setCardBackgroundColor(unselectedBg)
                card.strokeColor = unselectedStroke
                card.strokeWidth = dpToPx(1)
            }
        }

        updateCard(binding.cardThemeLight, mode == AppCompatDelegate.MODE_NIGHT_NO)
        updateCard(binding.cardThemeDark, mode == AppCompatDelegate.MODE_NIGHT_YES)
        updateCard(binding.cardThemeAuto, mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun isDarkTheme(): Boolean {
        val nightModeFlags = requireContext().resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun setupDisplayOptions() {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        
        binding.cbShowBalance.isChecked = sharedPref.getBoolean("show_balance", true)
        binding.cbCompactView.isChecked = sharedPref.getBoolean("compact_view", false)
        binding.cbShowStreaks.isChecked = sharedPref.getBoolean("show_streaks", true)

        binding.cbShowBalance.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("show_balance", isChecked).apply()
        }
        
        binding.cbCompactView.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("compact_view", isChecked).apply()
        }
        
        binding.cbShowStreaks.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("show_streaks", isChecked).apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
