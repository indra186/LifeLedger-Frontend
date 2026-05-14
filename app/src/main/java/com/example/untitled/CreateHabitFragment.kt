package com.example.untitled

import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentCreateHabitBinding
import com.example.untitled.viewmodels.CreateHabitViewModel
import com.example.untitled.viewmodels.UIState
import java.util.Calendar
import android.widget.ImageView

class CreateHabitFragment : Fragment() {

    private var _binding: FragmentCreateHabitBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateHabitViewModel

    private var selectedIcon = "fitness"
    private var selectedDays =
        mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentCreateHabitBinding.inflate(
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

        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(this)[CreateHabitViewModel::class.java]

        setupDropdowns()
        binding.dropdownFrequency.setOnItemClickListener { _, _, position, _ ->

            val selected =
                binding.dropdownFrequency.text.toString()

            if(selected == "Custom") {

                binding.layoutCustomDays.visibility =
                    View.VISIBLE

            } else {

                binding.layoutCustomDays.visibility =
                    View.GONE
            }
        }

        setupReminderPicker()

        setupObservers()

        setupIcons()

        binding.btnCreateHabit.setOnClickListener {

            val title =
                binding.etHabitName.text.toString().trim()

            val description =
                binding.etDescription.text.toString().trim()

            val frequency =
                binding.dropdownFrequency.text
                    .toString()
                    .lowercase()

            val goal =
                binding.etGoal.text.toString()
                    .ifEmpty { "1" }
                    .toInt()

            val unit =
                binding.dropdownGoalUnit.text.toString()

            val reminder =
                binding.etReminder.text
                    .toString()
                    .trim()
                    .ifEmpty { null }
            if (title.isEmpty()) {

                binding.etHabitName.error =
                    "Enter habit name"

                return@setOnClickListener
            }
            selectedDays.clear()


            if(binding.cbMon.isChecked) selectedDays.add("Mon")
            if(binding.cbTue.isChecked) selectedDays.add("Tue")
            if(binding.cbWed.isChecked) selectedDays.add("Wed")
            if(binding.cbThu.isChecked) selectedDays.add("Thu")
            if(binding.cbFri.isChecked) selectedDays.add("Fri")
            if(binding.cbSat.isChecked) selectedDays.add("Sat")
            if(binding.cbSun.isChecked) selectedDays.add("Sun")
            if(
                frequency == "custom" &&
                selectedDays.isEmpty()
            ) {

                Toast.makeText(
                    context,
                    "Select at least one day",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            val selectedDaysString =
                selectedDays.joinToString(",")
            viewModel.saveHabit(
                title,
                description,
                frequency,
                selectedDaysString,
                goal,
                unit,
                reminder,
                selectedIcon
            )
        }
    }

    private fun setupDropdowns() {

        val frequencies =
            listOf("Daily", "Custom")

        binding.dropdownFrequency.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                frequencies
            )
        )

        val units =
            listOf(
                "times",
                "minutes",
                "pages",
                "liters",
                "km"
            )

        binding.dropdownGoalUnit.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                units
            )
        )
    }

    private fun setupReminderPicker() {

        binding.etReminder.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            val hour =
                calendar.get(Calendar.HOUR_OF_DAY)

            val minute =
                calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->

                    val time =
                        String.format(
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                        )

                    binding.etReminder.setText(time)

                },
                hour,
                minute,
                false
            ).show()
        }
    }

    private fun setupIcons() {

        val icons = listOf(
            binding.iconFitness,
            binding.iconReading,
            binding.iconArt,
            binding.iconJournal,
            binding.iconMusic,
            binding.iconNature,
            binding.iconRunning,
            binding.iconWater,
            binding.iconWork,
            binding.iconAlarm
        )

        icons.forEach { imageView ->

            imageView.setOnClickListener {

                resetIcons(icons)

                selectedIcon =
                    when(imageView.id) {

                        R.id.icon_fitness -> "fitness"
                        R.id.icon_reading -> "reading"
                        R.id.icon_art -> "art"
                        R.id.icon_journal -> "journal"
                        R.id.icon_music -> "music"
                        R.id.icon_nature -> "nature"
                        R.id.icon_running -> "running"
                        R.id.icon_water -> "water"
                        R.id.icon_work -> "work"

                        else -> "alarm"
                    }

                // selected background
                imageView.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.brand_purple
                        )
                    )

                // selected icon color
                imageView.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.white
                    )
                )
            }
        }
    }

    private fun resetIcons(
        icons: List<ImageView>
    ) {

        icons.forEach {

            // reset background
            it.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_purple_bg
                    )
                )

            // reset icon color
            it.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
        }
    }

    private fun setupObservers() {

        viewModel.saveState.observe(viewLifecycleOwner) {

            when(it) {

                is UIState.Loading -> {

                    binding.btnCreateHabit.isEnabled = false
                }

                is UIState.Success -> {

                    binding.btnCreateHabit.isEnabled = true

                    Toast.makeText(
                        context,
                        "Habit created!",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().popBackStack()
                }

                is UIState.Error -> {

                    binding.btnCreateHabit.isEnabled = true

                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}