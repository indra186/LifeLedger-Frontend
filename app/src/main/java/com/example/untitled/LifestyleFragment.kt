package com.example.untitled

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentLifestyleBinding

class LifestyleFragment : Fragment() {

    private var _binding: FragmentLifestyleBinding? = null
    private val binding get() = _binding!!
    private var progressAnimator: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLifestyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Animate the progress bar slowly
        val progressBar = binding.pbDailyProgress
        progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 80).apply {
            duration = 2000 // 2 seconds
            interpolator = DecelerateInterpolator()
            start()
        }

        binding.cardHabits.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.action_lifestyleFragment_to_habitsFragment)
        }

        binding.cardTasks.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.action_lifestyleFragment_to_tasksFragment)
        }

        binding.cardHealth.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.action_lifestyleFragment_to_healthFragment)
        }

        binding.tvViewAllHabits.setOnClickListener {
             if (isAdded) findNavController().navigate(R.id.action_lifestyleFragment_to_habitsFragment)
        }
        
        binding.tvViewAllTasks.setOnClickListener {
             if (isAdded) findNavController().navigate(R.id.action_lifestyleFragment_to_tasksFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnimator?.cancel()
        _binding = null
    }
}
