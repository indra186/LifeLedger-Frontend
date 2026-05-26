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
import com.example.untitled.models.Habit

class HabitDetailFragment : Fragment() {

    private var _binding:
            FragmentHabitDetailBinding? = null
    private var weekHistoryData: List<String> = emptyList()
    private var currentHabit: Habit? = null

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
        binding.btnDelete.setOnClickListener {

            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage(
                    "Are you sure you want to delete this habit?"
                )
                .setPositiveButton(
                    "Delete"
                ) { _, _ ->

                    viewModel.deleteHabit(
                        habitId
                    )
                    androidx.work.WorkManager
                            .getInstance(requireContext())
                        .cancelUniqueWork(
                            "habit_$habitId"
                        )

                    findNavController().navigateUp()
                }
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .show()
        }
        habitId =
            arguments?.getInt(
                "habitId"
            ) ?: -1
        viewModel.fetchHabits()
        binding.tvWeekLoading.visibility =
            View.VISIBLE

        binding.layoutWeekHistory.visibility =
            View.GONE
        viewModel.fetchWeekHistory(
            habitId
        )
        binding.tvWeekLoading.postDelayed({

            if(isAdded) {

                binding.tvWeekLoading.visibility =
                    View.GONE

                binding.layoutWeekHistory.visibility =
                    View.VISIBLE
            }

        }, 600)

        observeHabit()
        observeWeekHistory()
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
                currentHabit = it

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

                if (
                    it.frequency == "custom"
                ) {

                    binding.tvCustomDays.visibility =
                        View.VISIBLE

                    binding.tvCustomDays.text =
                        it.selected_days
                }

                binding.tvStatus.visibility =
                    View.VISIBLE

                if (it.completed_today == 1) {

                    binding.tvStatus.text =
                        "Completed Today"

                    binding.tvStatus.setTextColor(
                        resources.getColor(
                            R.color.income_green,
                            null
                        )
                    )

                } else {

                    binding.tvStatus.text =
                        "Not done yet today"

                    binding.tvStatus.setTextColor(
                        resources.getColor(
                            R.color.expense_red,
                            null
                        )
                    )
                }

                setHabitIcon(it.icon)
                renderWeekHistory()

            }
        }
    }

    private fun observeWeekHistory() {

        viewModel.weekHistory.observe(
            viewLifecycleOwner
        ) { history ->

            weekHistoryData = history

            renderWeekHistory()
        }
    }


    private fun renderWeekHistory() {

        val habit = currentHabit ?: return

        val inputFormat =
            java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
            )

        val calendar =
            java.util.Calendar.getInstance()

        val todayDate =
            inputFormat.format(calendar.time)

        val completedDates =
            weekHistoryData.toSet()

        val dayViews = listOf(

            binding.tvMon,
            binding.tvTue,
            binding.tvWed,
            binding.tvThu,
            binding.tvFri,
            binding.tvSat,
            binding.tvSun
        )

        /*
            Move calendar to Monday
         */

        calendar.firstDayOfWeek =
            java.util.Calendar.MONDAY

        calendar.set(
            java.util.Calendar.DAY_OF_WEEK,
            java.util.Calendar.MONDAY
        )

        dayViews.forEachIndexed { index, view ->

            val currentDate =
                inputFormat.format(calendar.time)

            val currentDay =
                java.text.SimpleDateFormat(
                    "EEE",
                    java.util.Locale.getDefault()
                ).format(calendar.time)

            view.alpha = 1f

            /*
                Custom frequency disabled days
             */

            if(
                habit.frequency == "custom" &&
                habit.selected_days
                    ?.contains(currentDay) != true
            ) {

                setDayStatus(
                    view,
                    "disabled"
                )

                calendar.add(
                    java.util.Calendar.DAY_OF_MONTH,
                    1
                )

                return@forEachIndexed
            }

            when {

                completedDates.contains(currentDate) -> {

                    setDayStatus(
                        view,
                        "completed"
                    )
                }

                currentDate < todayDate -> {

                    setDayStatus(
                        view,
                        "missed"
                    )
                }

                currentDate == todayDate -> {

                    setDayStatus(
                        view,
                        "today_pending"
                    )
                }

                else -> {

                    setDayStatus(
                        view,
                        "future"
                    )
                }
            }

            calendar.add(
                java.util.Calendar.DAY_OF_MONTH,
                1
            )
        }

        binding.tvWeekLoading.visibility =
            View.GONE

        binding.layoutWeekHistory.visibility =
            View.VISIBLE
    }

    private fun setDayStatus(
        textView: android.widget.TextView,
        state: String
    ) {

        when (state) {

            "completed" -> {

                textView.setBackgroundResource(
                    R.drawable.bg_dot_green
                )
            }

            "missed" -> {

                textView.setBackgroundResource(
                    R.drawable.bg_dot_red
                )
            }

            "today_pending" -> {

                textView.setBackgroundResource(
                    R.drawable.bg_dot_outline
                )
            }

            "future" -> {

                textView.setBackgroundResource(
                    R.drawable.bg_dot_neutral
                )

                textView.alpha = 0.35f
            }

            "disabled" -> {

                textView.setBackgroundResource(
                    R.drawable.bg_dot_disabled
                )

                textView.alpha = 0.15f
            }
        }
    }

    private fun setHabitIcon(
        icon: String
    ) {

        val drawable = when (icon) {

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