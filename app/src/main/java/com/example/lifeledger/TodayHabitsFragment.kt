package com.example.lifeledger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifeledger.adapters.HabitsAdapter
import com.example.lifeledger.databinding.FragmentTodayHabitsBinding
import com.example.lifeledger.viewmodels.HabitsViewModel
import androidx.navigation.fragment.findNavController


class TodayHabitsFragment : Fragment() {

    private var _binding:
            FragmentTodayHabitsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter:
            HabitsAdapter

    private lateinit var viewModel:
            HabitsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentTodayHabitsBinding.inflate(
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

        adapter =
            HabitsAdapter(
                emptyList(),
                true,

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

                    viewModel.checkHabit(habit.id)

                    viewModel.fetchHabits()
                }
            )
        binding.rvTodayHabits.layoutManager =
            LinearLayoutManager(context)

        binding.rvTodayHabits.adapter =
            adapter

        observeHabits()

        viewModel.fetchHabits()
    }

    private fun observeHabits() {

        viewModel.todayHabits.observe(
            viewLifecycleOwner
        ) {

            adapter.updateHabits(it)
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}