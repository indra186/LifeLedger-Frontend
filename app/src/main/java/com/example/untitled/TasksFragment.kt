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
import com.example.untitled.adapters.TasksAdapter
import com.example.untitled.databinding.FragmentTasksBinding
import com.example.untitled.viewmodels.TasksViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var tasksAdapter: TasksAdapter
    
    private lateinit var viewModel: TasksViewModel
    
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
        tasksAdapter = TasksAdapter(emptyList()) { task, isChecked ->
            viewModel.updateTaskStatus(task, isChecked)
        }
        binding.rvTasks.layoutManager = LinearLayoutManager(context)
        binding.rvTasks.adapter = tasksAdapter

        observeTasks()
    }

    private fun observeTasks() {
        // Since we are using Room + StateFlow, initial loading might be fast.
        // We can toggle visibility based on empty list.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collectLatest { tasks ->
                pbLoading.visibility = View.GONE
                if (tasks.isEmpty()) {
                    binding.rvTasks.visibility = View.GONE
                    layoutEmptyState.visibility = View.VISIBLE
                } else {
                    binding.rvTasks.visibility = View.VISIBLE
                    layoutEmptyState.visibility = View.GONE
                    tasksAdapter.updateTasks(tasks)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
