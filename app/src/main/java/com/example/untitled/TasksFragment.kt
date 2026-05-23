package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.SectionedTasksAdapter
import com.example.untitled.adapters.TasksAdapter
import com.example.untitled.databinding.FragmentTasksBinding
import com.example.untitled.viewmodels.TasksViewModel
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TasksViewModel
    private lateinit var adapter:
            SectionedTasksAdapter

    
    private lateinit var pbLoading: ProgressBar
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[TasksViewModel::class.java]
        
        pbLoading = view.findViewById(R.id.pb_loading)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)
        
        binding.ivBack.setOnClickListener {
             if (isAdded) findNavController().navigateUp()
        }

        binding.tvAddTask.setOnClickListener {
             if (isAdded) findNavController().navigate(R.id.action_tasksFragment_to_addTaskFragment)
        }

        // Initialize RecyclerView
        adapter =
            SectionedTasksAdapter(
                emptyList()
            ) { task, checked ->

                viewModel.updateTaskStatus(
                    task,
                    checked
                )
            }

        binding.rvTasks.layoutManager =
            LinearLayoutManager(context)

        binding.rvTasks.adapter =
            adapter
        setupSwipeDelete()

        observeTasks()

        viewModel.fetchTasks()
        val filter =
            arguments?.getString("filter")
    }

    private fun observeTasks() {

        viewModel.sections.observe(
            viewLifecycleOwner
        ) { sections ->

            pbLoading.visibility =
                View.GONE

            val todaySection =
                sections.find {
                    it.title.startsWith("Today")
                }

            val todayTasks =
                todaySection?.tasks ?: emptyList()

            val completedTodayTasks =
                sections
                    .find {
                        it.title.startsWith("Completed")
                    }
                    ?.tasks
                    ?.filter {
                        isToday(it.date)
                    }
                    ?: emptyList()

            val totalTasks =
                todayTasks.size +
                        completedTodayTasks.size

            val completedTasks =
                completedTodayTasks.size

            binding.tvTasksCompletedLabel.text =
                "$completedTasks OF $totalTasks COMPLETED"

            binding.pbTasks.max =
                if(totalTasks == 0) 1
                else totalTasks

            binding.pbTasks.progress =
                completedTasks
            if(totalTasks == 0) {

                binding.tvTasksCompletedLabel.text =
                    "No tasks for today"

            } else {

                binding.tvTasksCompletedLabel.text =
                    "$completedTasks OF $totalTasks COMPLETED"
            }
//
            val allTasksCount =
                sections.sumOf {
                    it.tasks.size
                }

            if(allTasksCount == 0) {

                binding.rvTasks.visibility =
                    View.GONE

                layoutEmptyState.visibility =
                    View.VISIBLE
            }
            else {

                binding.rvTasks.visibility =
                    View.VISIBLE

                layoutEmptyState.visibility =
                    View.GONE

                val filter =
                    arguments?.getString("filter")

                if(filter == "today") {

                    val todayOnly =
                        sections.filter {
                            it.title.startsWith("Today")
                        }

                    adapter.updateSections(
                        todayOnly
                    )

                } else {

                    adapter.updateSections(
                        sections
                    )
                }
            }
        }

        viewModel.progress.observe(
            viewLifecycleOwner
        ) {

            binding.pbTasks.progress = it

        }


    }
    private fun setupSwipeDelete() {

        val callback =

            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {

                    val position =
                        viewHolder.adapterPosition

                    val item =
                        adapter.getItem(position)

                    if(
                        item is com.example.untitled.models.Task
                    ) {

                        viewModel.deleteTask(item)
                    }
                }
            }

        ItemTouchHelper(callback)
            .attachToRecyclerView(
                binding.rvTasks
            )
    }

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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
