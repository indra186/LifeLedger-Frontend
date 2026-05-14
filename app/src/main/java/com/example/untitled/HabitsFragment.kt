package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.HabitsAdapter
import com.example.untitled.databinding.FragmentHabitsBinding
import com.example.untitled.viewmodels.HabitsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitsAdapter: HabitsAdapter
    
    private lateinit var viewModel: HabitsViewModel
    
    private lateinit var pbLoading: ProgressBar
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[HabitsViewModel::class.java]
        
        pbLoading = view.findViewById(R.id.pb_loading)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)

        binding.btnBack.setOnClickListener {
            if (isAdded) findNavController().navigateUp()
        }

        binding.btnAddHabit.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.action_habitsFragment_to_createHabitFragment)
        }

        habitsAdapter =
            HabitsAdapter(
                emptyList(),
                false,

                { habit ->

                    val bundle = Bundle()

                    bundle.putInt(
                        "habitId",
                        habit.id
                    )

                    findNavController().navigate(
                        R.id.habitDetailFragment,
                        bundle
                    )
                },

                { habit ->

                    Toast.makeText(
                        context,
                        "Habit checked: ${habit.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        binding.rvHabits.layoutManager = LinearLayoutManager(context)
        binding.rvHabits.adapter = habitsAdapter
        viewModel.completion.observe(
            viewLifecycleOwner
        ) {

            binding.tvCompletionPercent.text =
                "$it%"
        }
        
        observeHabits()
        viewModel.fetchHabits()
    }

    private fun observeHabits() {

        viewModel.loading.observe(
            viewLifecycleOwner
        ) {

            pbLoading.visibility =
                if(it) View.VISIBLE
                else View.GONE
        }

        viewModel.habits.observe(
            viewLifecycleOwner
        ) { habits ->

            if(habits.isEmpty()) {

                binding.rvHabits.visibility =
                    View.GONE

                layoutEmptyState.visibility =
                    View.VISIBLE

            } else {

                binding.rvHabits.visibility =
                    View.VISIBLE

                layoutEmptyState.visibility =
                    View.GONE

                habitsAdapter.updateHabits(
                    habits
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
