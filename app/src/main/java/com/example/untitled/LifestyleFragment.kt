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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.HabitsAdapter
import com.example.untitled.viewmodels.HabitsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
class LifestyleFragment : Fragment() {

    private var _binding: FragmentLifestyleBinding? = null
    private val binding get() = _binding!!
    private var progressAnimator: ObjectAnimator? = null
    private lateinit var habitsAdapter: HabitsAdapter

    private lateinit var habitsViewModel: HabitsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLifestyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        habitsViewModel =
            ViewModelProvider(this)
                .get(HabitsViewModel::class.java)
        habitsAdapter =
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

                    habitsViewModel.checkHabit(
                        habit.id
                    )
                }
            )

        binding.rvTodayHabits.layoutManager =
            LinearLayoutManager(context)

        binding.rvTodayHabits.adapter =
            habitsAdapter
        habitsViewModel.fetchHabits()

//        observeHabits()

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
        habitsViewModel.todayHabits.observe(
            viewLifecycleOwner
        ) { habits ->

            habitsAdapter.updateHabits(
                habits.take(3)
            )

            binding.tvHabitsTitle.visibility =

                if(habits.isEmpty())
                    View.GONE
                else
                    View.VISIBLE

            binding.rvTodayHabits.visibility =

                if(habits.isEmpty())
                    View.GONE
                else
                    View.VISIBLE

            binding.tvViewAllHabits.visibility =

                if(habits.size > 3)
                    View.VISIBLE
                else
                    View.GONE
        }

        binding.tvViewAllHabits.setOnClickListener {

            if(isAdded) {

                findNavController().navigate(
                    R.id.todayHabitsFragment
                )
            }
        }
        habitsViewModel.completion.observe(
            viewLifecycleOwner
        ) {

            binding.pbDailyProgress.progress = it

            binding.tvProgressVal.text =
                "$it%"
        }
        binding.tvViewAllTasks.setOnClickListener {
             if (isAdded) findNavController().navigate(R.id.action_lifestyleFragment_to_tasksFragment)
        }

    }
//    private fun observeHabits() {
//
//        habitsViewModel.todayHabits.observe(
//            viewLifecycleOwner
//        ) { habits ->
//
//            val previewHabits =
//                habits.take(3)
//
//            habitsAdapter.updateHabits(
//                previewHabits
//            )
//
//            binding.tvViewAllHabits.visibility =
//
//                if(habits.size > 3)
//                    View.VISIBLE
//                else
//                    View.GONE
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnimator?.cancel()
        _binding = null
    }
}
