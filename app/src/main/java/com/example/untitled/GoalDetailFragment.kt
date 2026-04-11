package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.GoalHistoryAdapter
import com.example.untitled.databinding.FragmentGoalDetailBinding
import com.example.untitled.models.*
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.EditText
import android.widget.Spinner
import android.widget.Button
import androidx.navigation.fragment.findNavController

class GoalDetailFragment : Fragment() {

    private var _binding: FragmentGoalDetailBinding? = null
    private val binding get() = _binding!!

    // 🔥 STEP 1: goalId
    private var goalId: Int = -1

    // 🔥 STEP 2: adapter
    private lateinit var historyAdapter: GoalHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ GET goalId
        goalId = arguments?.getInt("goalId") ?: -1

        // ✅ LOAD HISTORY
        loadHistory()
        loadGoalDetails()


        binding.btnContribution.setOnClickListener {
            showContributionDialog()
        }
        binding.btnDelete.setOnClickListener {

            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal?")
                .setPositiveButton("Yes") { _, _ ->

                    val request = DeleteGoalRequest(goalId)

                    RetrofitClient.instance.deleteGoal(request)
                        .enqueue(object : Callback<GenericResponse> {

                            override fun onResponse(
                                call: Call<GenericResponse>,
                                response: Response<GenericResponse>
                            ) {
                                if (response.isSuccessful && response.body()?.success == true) {

                                    Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show()

                                    findNavController().popBackStack() // go back
                                }
                            }

                            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                                Toast.makeText(context, "Error deleting goal", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadGoalDetails() {
        RetrofitClient.instance.getGoalDetail(goalId)
            .enqueue(object : Callback<GoalDetailResponse> {

                override fun onResponse(
                    call: Call<GoalDetailResponse>,
                    response: Response<GoalDetailResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val goal = response.body()!!.data

                        // 🔥 SET UI
                        binding.tvGoalTitle.text = goal.title
                        binding.tvGoalProgress.text =
                            "₹${goal.current_amount} saved of ₹${goal.target_amount}"

                        val progress = if (goal.target_amount > 0) {
                            ((goal.current_amount / goal.target_amount) * 100).toInt()
                        } else 0

                        binding.progressBar.progress = progress
                    }
                }

                override fun onFailure(call: Call<GoalDetailResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to load goal", Toast.LENGTH_SHORT).show()
                }
            })
    }
    // 🔥 STEP 3: LOAD HISTORY FUNCTION
    private fun loadHistory() {
        RetrofitClient.instance.getGoalHistory(goalId)
            .enqueue(object : Callback<GoalHistoryResponse> {

                override fun onResponse(
                    call: Call<GoalHistoryResponse>,
                    response: Response<GoalHistoryResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val history = response.body()!!.data.progress
                        setupHistoryRecycler(history)
                    }
                }

                override fun onFailure(call: Call<GoalHistoryResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to load history", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // 🔥 STEP 4: SETUP RECYCLER
    private fun setupHistoryRecycler(list: List<GoalProgress>) {
        if (!::historyAdapter.isInitialized) {
            historyAdapter = GoalHistoryAdapter(list)
            binding.rvHistory.layoutManager = LinearLayoutManager(context)
            binding.rvHistory.adapter = historyAdapter
        } else {
            historyAdapter.update(list)
        }
    }
    private fun showContributionDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contribution, null)

        val etAmount = dialogView.findViewById<EditText>(R.id.et_amount)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_accounts)
        val btnAdd = dialogView.findViewById<Button>(R.id.btn_add)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // 🔥 LOAD ACCOUNTS FROM API
        RetrofitClient.instance.getAccounts()
            .enqueue(object : Callback<AccountsResponse> {

                override fun onResponse(
                    call: Call<AccountsResponse>,
                    response: Response<AccountsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val accounts = response.body()!!.data

                        val names = accounts.map {
                            "${it.account_name} (₹${it.balance})"
                        }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            names
                        )

                        spinner.adapter = adapter

                        btnAdd.setOnClickListener {

                            val amount = etAmount.text.toString().toDoubleOrNull()

                            if (amount == null || amount <= 0) {
                                Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            val selectedAccount = accounts[spinner.selectedItemPosition]

                            addContribution(amount, selectedAccount.id, dialog)
                        }
                    }
                }

                override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to load accounts", Toast.LENGTH_SHORT).show()
                }
            })

        dialog.show()
    }
    private fun addContribution(amount: Double, accountId: Int, dialog: android.app.AlertDialog) {

        val request = AddGoalProgressRequest(
            goal_id = goalId,
            amount = amount,
            account_id = accountId
        )

        RetrofitClient.instance.addGoalProgress(request)
            .enqueue(object : Callback<GenericResponse> {

                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        Toast.makeText(context, "Contribution added!", Toast.LENGTH_SHORT).show()

                        dialog.dismiss()

                        loadHistory() // refresh history
                        loadGoalDetails()// refresh the added contribution

                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}