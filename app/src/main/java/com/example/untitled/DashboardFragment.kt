package com.example.untitled

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.databinding.FragmentDashboardBinding
import com.example.untitled.models.DashboardResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Handle notification click
        binding.ivNotification.setOnClickListener {
             if (isAdded) findNavController().navigate(R.id.action_dashboardFragment_to_notificationsFragment)
        }
        
        // Handle View All Goals click
        binding.tvViewAllGoals.setOnClickListener {
             if (isAdded) findNavController().navigate(R.id.action_dashboardFragment_to_goalsFragment)
        }

        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        RetrofitClient.instance.getDashboardData()
            .enqueue(object : Callback<DashboardResponse> {

                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (!isAdded || _binding == null) return

                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!.data
                        Log.d("DASHBOARD", "User from API = ${data.name}")

                        // USER NAME
                        binding.tvUsername.text = data.name
                        // Total Balance
                        binding.tvBalanceAmount.text = "₹${data.total_balance}"

                        // Monthly spending = sum of expenses
                        val expense = data.recent_transactions
                            .filter { it.type == "expense" }
                            .sumOf { it.amount }

                        binding.tvExpenseVal.text = "₹$expense"

                        // Goal
                        if (data.goals.isNotEmpty()) {
                            val goal = data.goals[0]
                            binding.tvGoalName.text = goal.title

                            val percent =
                                ((goal.current_amount / goal.target_amount) * 100).toInt()

                            binding.progressBarGoal.progress = percent
                            binding.tvGoalPercent.text = "$percent%"
                            binding.tvGoalProgress.text =
                                "${goal.current_amount.toInt()} of ${goal.target_amount.toInt()}"
                        }

                    } else {
                        Log.e("Dashboard", "Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    if (!isAdded || _binding == null) return
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
