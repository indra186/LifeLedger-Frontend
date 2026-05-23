package com.example.untitled

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentTaskAddBinding
import com.example.untitled.viewmodels.AddTaskViewModel
import com.example.untitled.viewmodels.UIState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.Activity
import android.content.Intent

class AddTaskFragment : Fragment() {

    private var _binding:
            FragmentTaskAddBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel:
            AddTaskViewModel

    private var selectedDate = ""
    private var selectedTime = ""

    private var repeatType = "none"
    private var repeatDays = ""

    private var priority = "medium"

    private var attachmentUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentTaskAddBinding.inflate(
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
            ViewModelProvider(this)[AddTaskViewModel::class.java]

        setupDateSelection()

        setupTimePicker()

        setupPriority()

        setupRepeat()

        setupAttachment()

        setupObservers()

        binding.ivBack.setOnClickListener {

            findNavController().navigateUp()
        }

        binding.btnSaveTask.setOnClickListener {

            saveTask()
        }
    }

    private fun setupDateSelection() {

        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        binding.chipToday.setOnClickListener {

            selectedDate =
                formatter.format(
                    Calendar.getInstance().time
                )
        }

        binding.chipTomorrow.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )

            selectedDate =
                formatter.format(
                    calendar.time
                )
        }

        binding.chipCustomDate.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->

                    val pickedCalendar =
                        Calendar.getInstance()

                    pickedCalendar.set(
                        year,
                        month,
                        day
                    )

                    selectedDate =
                        formatter.format(
                            pickedCalendar.time
                        )

                    binding.chipCustomDate.text =
                        selectedDate

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        selectedDate =
            formatter.format(
                Calendar.getInstance().time
            )
    }

    private fun setupTimePicker() {

        binding.etTime.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->

                    selectedTime =
                        String.format(
                            "%02d:%02d",
                            hour,
                            minute
                        )

                    binding.etTime.setText(
                        selectedTime
                    )
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun setupPriority() {

        binding.chipMedium.isChecked = true

        binding.chipHigh.setOnClickListener {

            priority = "high"
        }

        binding.chipMedium.setOnClickListener {

            priority = "medium"
        }

        binding.chipLow.setOnClickListener {

            priority = "low"
        }
    }

    private fun setupRepeat() {

        binding.layoutRepeat.setOnClickListener {

            val bottomSheet =
                RepeatBottomSheet {

                    repeatType = it.first

                    repeatDays = it.second

                    binding.tvRepeatValue.text =

                        when(repeatType) {

                            "daily" ->
                                "Daily"

                            "weekly" ->
                                repeatDays

                            "monthly" -> {

                                val day =
                                    selectedDate.takeLast(2).toInt()

                                val suffix =
                                    when {
                                        day in 11..13 -> "th"
                                        day % 10 == 1 -> "st"
                                        day % 10 == 2 -> "nd"
                                        day % 10 == 3 -> "rd"
                                        else -> "th"
                                    }

                                "Every month on ${day}${suffix}"
                            }

                            else ->
                                "No Repeat"
                        }
                }

            bottomSheet.show(
                parentFragmentManager,
                "RepeatBottomSheet"
            )
        }
    }

    private fun setupAttachment() {

        binding.layoutAttachment.setOnClickListener {

            val intent =
                Intent(
                    Intent.ACTION_PICK
                )

            intent.type = "image/*"

            imagePicker.launch(intent)
        }
    }

    private fun saveTask() {

        val title =
            binding.etTaskTitle.text
                .toString()
                .trim()

        val description =
            binding.etDescription.text
                .toString()
                .trim()

        val reminderEnabled =
            binding.switchReminder.isChecked

        viewModel.saveTask(
            title = title,
            description = description,
            date = selectedDate,
            time = selectedTime,
            repeatType = repeatType,
            repeatDays = repeatDays,
            priority = priority,
            reminderEnabled = reminderEnabled,
            attachmentUri = attachmentUri
        )
    }

    private fun setupObservers() {

        viewModel.saveTaskState.observe(
            viewLifecycleOwner
        ) {

            when(it) {

                is UIState.Loading -> {

                    binding.btnSaveTask.isEnabled =
                        false
                }

                is UIState.Success -> {

                    binding.btnSaveTask.isEnabled =
                        true

                    Toast.makeText(
                        context,
                        "Task Created",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigateUp()
                }

                is UIState.Error -> {

                    binding.btnSaveTask.isEnabled =
                        true

                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private val imagePicker =

        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if(
                result.resultCode == Activity.RESULT_OK
            ) {

                val uri =
                    result.data?.data

                uri?.let {

                    attachmentUri =
                        it.toString()

                    binding.ivAttachmentPreview.visibility =
                        View.VISIBLE

                    binding.ivAttachmentPreview.setImageURI(it)
                }
            }
        }
    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}