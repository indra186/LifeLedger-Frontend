package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.databinding.FragmentGoalsBinding
import com.example.untitled.viewmodels.GoalsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.untitled.adapters.GoalsAdapter
import com.example.untitled.models.Goal

class GoalsFragment : Fragment() {
    
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: GoalsViewModel
    private lateinit var goalsAdapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[GoalsViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        
        binding.btnBack.setOnClickListener {
             if (isAdded) findNavController().navigateUp()
        }
        
        binding.btnAddGoal.setOnClickListener {
            if (isAdded) findNavController().navigate(R.id.action_goalsFragment_to_addGoalFragment)
        }
        viewModel.loadGoals()
    }

    private fun setupRecyclerView() {
        goalsAdapter = GoalsAdapter(emptyList()) { goal ->
            val bundle = Bundle()
            bundle.putString("title", goal.title)
            bundle.putDouble("current", goal.current_amount)
            bundle.putDouble("target", goal.target_amount)
            bundle.putInt("goalId", goal.id.toInt())

            findNavController().navigate(
                R.id.action_goalsFragment_to_goalDetailFragment,
                bundle
            )
        }
        binding.rvGoals.layoutManager = LinearLayoutManager(context)
        binding.rvGoals.adapter = goalsAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.goals.collectLatest { goals: List<Goal> ->
                goalsAdapter.updateGoals(goals)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//class GoalsAdapter(private var goals: List<GoalEntity>) : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {
//
//    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvName: TextView = itemView.findViewById(R.id.tv_goal_name)
//        val tvAmount: TextView = itemView.findViewById(R.id.tv_goal_amount)
//        val pbProgress: ProgressBar = itemView.findViewById(R.id.pb_goal_progress)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal_card, parent, false)
//        return GoalViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
//        val goal = goals[position]
//        holder.tvName.text = goal.title
//        holder.tvAmount.text = "$${goal.currentAmount} / $${goal.targetAmount}"
//
//        val progress = if (goal.targetAmount > 0) {
//            ((goal.currentAmount / goal.targetAmount) * 100).toInt()
//        } else 0
//        holder.pbProgress.progress = progress
//    }
//
//    override fun getItemCount() = goals.size
//
//    fun updateGoals(newGoals: List<GoalEntity>) {
//        goals = newGoals
//        notifyDataSetChanged()
//    }
//}
