package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentHabitDetailBinding
import com.example.untitled.viewmodels.HabitsViewModel

class HabitDetailFragment : Fragment() {

    private var _binding:
            FragmentHabitDetailBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel:
            HabitsViewModel
    private var habitId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentHabitDetailBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        viewModel =
            ViewModelProvider(this)
                .get(HabitsViewModel::class.java)

        binding.btnBack.setOnClickListener {

            findNavController().navigateUp()
        }
        habitId =
            arguments?.getInt(
                "habitId"
            ) ?: -1
        viewModel.fetchHabits()

        observeHabit()
    }

    private fun observeHabit() {

        viewModel.habits.observe(
            viewLifecycleOwner
        ) { habits ->

            val habit =
                habits.find {
                    it.id == habitId
                }

            habit?.let {

                binding.tvHabitName.text =
                    it.name

                binding.tvDescription.text =
                    it.description

                binding.tvStreak.text =
                    "🔥 ${it.streak} Day Streak"

                binding.tvGoal.text =
                    "${it.goal_per_day} ${it.goal_unit}/day"

                binding.tvReminder.text =
                    it.reminder_time ?: "No reminder"

                binding.tvFrequency.text =
                    it.frequency.replaceFirstChar { c ->
                        c.uppercase()
                    }

                if(
                    it.frequency == "custom"
                ) {

                    binding.tvCustomDays.visibility =
                        View.VISIBLE

                    binding.tvCustomDays.text =
                        it.selected_days
                }

                if(it.completed_today == 1) {

                    binding.tvStatus.visibility =
                        View.VISIBLE
                }

                setHabitIcon(it.icon)
            }
        }
    }

    private fun setHabitIcon(
        icon: String
    ) {

        val drawable = when(icon) {

            "fitness" ->
                R.drawable.ic_habit_fitness

            "reading" ->
                R.drawable.ic_habit_reading

            "water" ->
                R.drawable.ic_habit_water

            "running" ->
                R.drawable.ic_habit_running

            "music" ->
                R.drawable.ic_habit_music

            "journal" ->
                R.drawable.ic_habit_journal

            "nature" ->
                R.drawable.ic_habit_nature

            else ->
                R.drawable.ic_habit_fitness
        }

        binding.ivIcon.setImageResource(
            drawable
        )
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}