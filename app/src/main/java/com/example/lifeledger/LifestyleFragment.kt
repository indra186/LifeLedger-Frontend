package com.example.lifeledger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeledger.databinding.FragmentLifestyleBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifeledger.adapters.HabitsAdapter
import com.example.lifeledger.adapters.SectionedTasksAdapter
import com.example.lifeledger.models.TaskSection
import com.example.lifeledger.viewmodels.HabitsViewModel
import com.example.lifeledger.viewmodels.TasksViewModel

class LifestyleFragment : Fragment() {

    private var _binding: FragmentLifestyleBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitsAdapter: HabitsAdapter
    private var habitsProgress = 0

    private var tasksProgress = 0

    private lateinit var habitsViewModel: HabitsViewModel
    private lateinit var tasksViewModel:
            TasksViewModel

    private lateinit var tasksAdapter:
            SectionedTasksAdapter
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

            habitsProgress = it

            updateOverallProgress()
        }
        binding.tvViewAllTasks.setOnClickListener {

            val bundle = Bundle()

            bundle.putString(
                "filter",
                "today"
            )

            if(isAdded) {

                findNavController().navigate(
                    R.id.action_lifestyleFragment_to_tasksFragment,
                    bundle
                )
            }
        }
        tasksViewModel =
            ViewModelProvider(this)
                .get(TasksViewModel::class.java)

        tasksAdapter =
            SectionedTasksAdapter(
                emptyList()
            ) { task, checked ->

                tasksViewModel.updateTaskStatus(
                    task,
                    checked
                )
            }
        binding.rvTodayTasks.layoutManager =
            LinearLayoutManager(context)

        binding.rvTodayTasks.adapter =
            tasksAdapter

        tasksViewModel.sections.observe(
            viewLifecycleOwner
        ) { sections ->

            val todaySection =
                sections.find {
                    it.title.startsWith("Today")
                }

            val previewTasks =
                todaySection
                    ?.tasks
                    ?.take(3)
                    ?: emptyList()
            binding.tvTasksTitle.text =

                if(previewTasks.isEmpty())
                    "All today's tasks completed"
                else
                    "Today's Tasks"

            tasksAdapter.updateSections(

                listOf(
                    TaskSection(
                        "Today's Tasks",
                        previewTasks
                    )
                )
            )

        }

        tasksViewModel.fetchTasks()
        tasksViewModel.progress.observe(
            viewLifecycleOwner
        ) { progress ->

            tasksProgress = progress

            updateOverallProgress()
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
private fun isToday(
    date: String?
): Boolean {

    if(date == null)
        return false

    val today =
        java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

    return date.startsWith(today)
}
    private fun updateOverallProgress() {

        val overall =
            (habitsProgress + tasksProgress) / 2


        binding.tvProgressVal.text =
            "$overall%"

        binding.tvMotivation.text =

            when {

                overall >= 90 ->
                    "Outstanding consistency today"

                overall >= 70 ->
                    "Great job! Keep up the momentum"

                overall >= 40 ->
                    "Good progress. Stay focused"

                overall > 0 ->
                    "Small steps still matter"

                else ->
                    "Start completing today's goals"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
