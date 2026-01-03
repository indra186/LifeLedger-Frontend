package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentHabitsBinding
import com.google.android.material.card.MaterialCardView

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddHabit.setOnClickListener {
            findNavController().navigate(R.id.action_habitsFragment_to_createHabitFragment)
        }
        
        setupHabitInteraction(binding.habitCard1, binding.habitCheckbox1)
        setupHabitInteraction(binding.habitCard2, binding.habitCheckbox2)
        setupHabitInteraction(binding.habitCard3, binding.habitCheckbox3)
    }

    private fun setupHabitInteraction(card: MaterialCardView, checkbox: CheckBox) {
        val clickListener = View.OnClickListener {
            // Change color to blue (light_blue_bg or brand_purple if preferred, let's use a nice blue)
            // User requested "blue". I'll use a color resource if available or parse color.
            // Using a resource color "light_blue_bg" for background tint might be good, but user said "blue".
            // Let's use #E3F2FD (light blue) or similar.
            
            card.setCardBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"))
            checkbox.isChecked = true
            
            // Disappear after a delay? Or immediately?
            // "disappear" -> set visibility to GONE.
            // "get a popup that completed"
            
            Toast.makeText(context, "Habit Completed!", Toast.LENGTH_SHORT).show()
            
            card.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    card.visibility = View.GONE
                }
                .start()
        }

        card.setOnClickListener(clickListener)
        checkbox.setOnClickListener(clickListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
