package com.example.untitled

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.untitled.models.DashboardResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private lateinit var tvTotalBalance: TextView
    private lateinit var tvExpense: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tvTotalBalance = view.findViewById(R.id.tv_balance_amount)
        tvExpense = view.findViewById(R.id.tv_expense_val)

        // Handle notification click
        view.findViewById<ImageView>(R.id.iv_notification).setOnClickListener {
             findNavController().navigate(R.id.action_dashboardFragment_to_notificationsFragment)
        }
        
        // Handle View All Goals click
        view.findViewById<TextView>(R.id.tv_view_all_goals).setOnClickListener {
             findNavController().navigate(R.id.action_dashboardFragment_to_goalsFragment)
        }

        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        // TODO: Get actual logged-in user ID
        val userId = "1" 

        RetrofitClient.instance.getDashboardData(userId).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    if (data != null) {
                        tvTotalBalance.text = "$${data.total_balance}"
                        tvExpense.text = "$${data.monthly_spending}"
                        // Update lists and other UI elements here
                    }
                } else {
                    Log.e("Dashboard", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                Log.e("Dashboard", "Failure: ${t.message}")
                Toast.makeText(context, "Failed to load dashboard data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
