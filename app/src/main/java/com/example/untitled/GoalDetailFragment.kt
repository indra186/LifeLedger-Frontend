package com.example.untitled

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.GoalHistoryAdapter
import com.example.untitled.databinding.FragmentGoalDetailBinding
import com.example.untitled.models.*
import com.example.untitled.network.RetrofitClient
import com.example.untitled.utils.AgentEngine
import com.example.untitled.utils.AgentRunner
import com.example.untitled.utils.GoalCalculator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.untitled.utils.InsightEngine
import com.example.untitled.utils.RecoveryPlanner
import android.os.Handler
import android.os.Looper
import com.example.untitled.utils.MonthUtils

class GoalDetailFragment : Fragment() {

    private var _binding: FragmentGoalDetailBinding? = null
    private val binding get() = _binding!!

    //  STEP 1: goalId
    private var goalId: Int = -1
    private var currentGoal: Goal? = null
    private var lastStrategy: String = "UNKNOWN"
    private var lastGoalInsight: GoalInsight? = null
    private var lastFinalInsight: FinalInsight? = null
    private var isOptimizing = false

    //  STEP 2: adapter
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
        android.util.Log.d("GOAL_DEBUG", "goalId = $goalId")
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
        binding.btnOptimize.setOnClickListener {

            if (isOptimizing) return@setOnClickListener

            isOptimizing = true

            checkDataAndOptimize()
            Log.d("AI_UI", "Optimize clicked for goalId: $goalId")
        }

    }
    private fun getUserId(): String {
        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)

        return prefs.getString("userId", "") ?: ""
    }

    private fun checkDataAndOptimize() {

        val calendar = java.util.Calendar.getInstance()

        val month = calendar.get(java.util.Calendar.MONTH)
        val year = calendar.get(java.util.Calendar.YEAR)

        val (startDate, endDate) =
            MonthUtils.getMonthDateRange(month, year)

        RetrofitClient.instance.getTransactionsByMonth(
            getUserId(),
            startDate,
            endDate
        ).enqueue(object : Callback<TransactionsResponse> {

                override fun onResponse(
                    call: Call<TransactionsResponse>,
                    response: Response<TransactionsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val transactions = response.body()?.data ?: emptyList()

                        if (transactions.isEmpty()) {
                            Toast.makeText(context, "No data to analyze", Toast.LENGTH_SHORT).show()
                            return
                        }

                        val lastDate = transactions.maxByOrNull { it.tx_date }?.tx_date

                        val isOutdated = isDataOutdated(lastDate)

                        if (isOutdated) {
                            showOutdatedWarning(transactions)
                        } else {
                            generateInsights(transactions)
                        }
                    }
                }

                override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun isDataOutdated(date: String?): Boolean {
        if (date == null) return true

        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

            val lastDate = sdf.parse(date) ?: return true
            val now = java.util.Date()

            val diff = now.time - lastDate.time
            val days = diff / (1000 * 60 * 60 * 24)

            days > 1
        } catch (e: Exception) {
            true
        }
    }
    private fun showOutdatedWarning(transactions: List<TransactionItem>) {

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("⚠ Data may be outdated")
            .setMessage("Make sure your transactions are up to date.\nContinue anyway?")
            .setPositiveButton("Continue") { _, _ ->
                generateInsights(transactions)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun generateInsights(transactions: List<TransactionItem>) {

        RetrofitClient.instance.getGoalDetail(goalId)
            .enqueue(object : Callback<GoalDetailResponse> {

                override fun onResponse(
                    call: Call<GoalDetailResponse>,
                    response: Response<GoalDetailResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val goal = response.body()!!.data

                        RetrofitClient.instance.getGoalHistory(goalId)
                            .enqueue(object : Callback<GoalHistoryResponse> {

                                override fun onResponse(
                                    call: Call<GoalHistoryResponse>,
                                    response: Response<GoalHistoryResponse>
                                ) {
                                    if (response.isSuccessful && response.body()?.success == true) {

                                        val history = response.body()!!.data.progress

                                        RetrofitClient.instance.getAccounts()
                                            .enqueue(object : Callback<AccountsResponse> {
                                                override fun onResponse(
                                                    call: Call<AccountsResponse>,
                                                    response: Response<AccountsResponse>
                                                ) {
                                                    if (response.isSuccessful && response.body()?.success == true) {

                                                        val accounts = response.body()!!.data
                                                        val totalBalance = accounts.sumOf { it.balance }

                                                        calculateFinalInsight(goal, history, transactions, totalBalance)
                                                    }
                                                }

                                                override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                                                    Toast.makeText(context, "Failed to load accounts", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                    }
                                }

                                override fun onFailure(call: Call<GoalHistoryResponse>, t: Throwable) {
                                    Toast.makeText(context, "Failed to load history", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onFailure(call: Call<GoalDetailResponse>, t: Throwable) {
                    Toast.makeText(context, "Error analyzing", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun calculateFinalInsight(
        goal: Goal,
        history: List<GoalProgress>,
        transactions: List<TransactionItem>,
        totalBalance: Double
    ) {
        val finalInsight = InsightEngine.generate(goal, history, transactions)
        lastFinalInsight = finalInsight

        val lastDate = transactions.maxByOrNull { it.tx_date }?.tx_date
        val lastUpdated = getLastUpdatedText(lastDate)

        renderInsight(finalInsight,goal,history.size,lastUpdated)
    }
    @SuppressLint("SetTextI18n")
    private fun renderInsight(
        final: FinalInsight,
        goal: Goal,
        historySize: Int,
        lastUpdated: String
    ) {

        val g = final.goalInsight
        val f = final.financeInsight
        val r = final.recoveryPlan
        lastGoalInsight = g

        val trendText = if (historySize < 4) "Not enough data" else g.trend.toString()

        if (g.state == GoalState.COMPLETED) {
            binding.tvAiContent.text = """
🎉 Goal Completed Early!

You saved ₹${goal.current_amount}

🏆 Strong financial discipline

Confidence: ${final.confidence}
Last updated: $lastUpdated
""".trimIndent()
            return
        }

        val message = """
📊 Goal Insight

Required: ₹${g.requiredPerDay.toInt()}/day  
You save: ₹${g.actualPerDay.toInt()}/day  

${final.risk}
${final.spendingImpact}

🛠 Recovery Plan:
${r.suggestion}

🧠 Behavior: ${final.behavior}
📈 Trend: $trendText

💣 Top Spending: ${f.topCategory}
₹${f.topCategorySpend.toInt()}

Confidence: ${final.confidence}
Last updated: $lastUpdated
""".trimIndent()

        binding.tvAiContent.text = message

// 🔥 STEP 1: CREATE TASKS
        AgentEngine.runAgent(
            final.goalInsight,
            final.financeInsight,
            final.recoveryPlan,
            RetrofitClient.instance
        ) {
            // nothing needed
        }
        Log.d("AI_UI", "Rendering insight: $final")

// 🔥 STEP 2: FETCH TASKS
        Handler(Looper.getMainLooper()).postDelayed({
            AgentRunner.fetchTasks(goalId, RetrofitClient.instance) {
                binding.tvAiContent.append("\n\n$it")
            }
        }, 3000)
        isOptimizing = false
    }

    private fun getLastUpdatedText(date: String?): String {

        if (date == null) return "Unknown"

        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

            val lastDate = sdf.parse(date) ?: return "Unknown"
            val now = java.util.Date()

            val diff = now.time - lastDate.time

            val minutes = diff / (1000 * 60)
            val hours = diff / (1000 * 60 * 60)
            val days = diff / (1000 * 60 * 60 * 24)

            when {
                minutes < 60 -> "$minutes min ago"
                hours < 24 -> "$hours hours ago"
                days == 1L -> "1 day ago"
                days < 7 -> "$days days ago"
                else -> {
                    val outFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    outFormat.format(lastDate)
                }
            }

        } catch (e: Exception) {
            "Unknown"
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
                        currentGoal = goal

                        // 🔥 SET UI
                        binding.tvGoalTitle.text = goal.title
                        binding.tvGoalProgress.text =
                            "₹${goal.current_amount} saved of ₹${goal.target_amount}"
                        val deadline = goal.deadline

                        binding.tvGoalDeadline.text = if (!deadline.isNullOrEmpty()) {

                            try {

                                val inputFormat =
                                    java.text.SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        java.util.Locale.getDefault()
                                    )

                                val outputFormat =
                                    java.text.SimpleDateFormat(
                                        "dd MMM yyyy",
                                        java.util.Locale.getDefault()
                                    )

                                val date = inputFormat.parse(deadline)

                                "Target Date: ${outputFormat.format(date!!)}"

                            } catch (e: Exception) {

                                "Target Date: $deadline"
                            }

                        } else {

                            "No deadline set"
                        }

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

                            val inputAmount = etAmount.text.toString().toDoubleOrNull()

                            if (inputAmount == null || inputAmount <= 0) {
                                Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            val goal = currentGoal
                            if (goal == null) {
                                Toast.makeText(context, "Goal not loaded", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            val remaining = goal.target_amount - goal.current_amount

                            val finalAmount = if (inputAmount > remaining) {
                                Toast.makeText(
                                    context,
                                    "Only ₹${remaining.toInt()} needed to complete goal",
                                    Toast.LENGTH_SHORT
                                ).show()
                                remaining
                            } else {
                                inputAmount
                            }

                            val selectedAccount = accounts[spinner.selectedItemPosition]

                            addContribution(finalAmount, selectedAccount.id, dialog)

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
            .enqueue(object : Callback<GoalProgressResponse> {

                override fun onResponse(
                    call: Call<GoalProgressResponse>,
                    response: Response<GoalProgressResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // 🔥 STEP 1: get latest insight again
                        val calendar = java.util.Calendar.getInstance()

                        val month = calendar.get(java.util.Calendar.MONTH)
                        val year = calendar.get(java.util.Calendar.YEAR)

                        val (startDate, endDate) =
                            MonthUtils.getMonthDateRange(month, year)

                        RetrofitClient.instance.getTransactionsByMonth(
                            getUserId(),
                            startDate,
                            endDate
                        ).enqueue(object : Callback<TransactionsResponse> {

                                override fun onResponse(
                                    call: Call<TransactionsResponse>,
                                    response: Response<TransactionsResponse>
                                ) {
                                    if (response.isSuccessful && response.body()?.success == true) {

                                        val transactions = response.body()?.data ?: emptyList()

                                        val goal = currentGoal ?: return
                                        RetrofitClient.instance.getGoalHistory(goalId)
                                            .enqueue(object : Callback<GoalHistoryResponse> {

                                                override fun onResponse(
                                                    call: Call<GoalHistoryResponse>,
                                                    response: Response<GoalHistoryResponse>
                                                ) {
                                                    if (response.isSuccessful && response.body()?.success == true) {

                                                        val history = response.body()!!.data.progress

                                                        val insight = InsightEngine.generate(goal, history, transactions)

                                                        // 🔥 THIS IS THE TRIGGER
//                                                        AgentEngine.runAgent(
//                                                            insight.goalInsight,
//                                                            insight.financeInsight,
//                                                            insight.recoveryPlan,
//                                                            RetrofitClient.instance
//                                                        ) {
//                                                            // Optional UI update
//                                                            binding.tvAiContent.append("\n\n🤖 New Task Created")
//                                                        }
                                                        val final = lastFinalInsight ?: return
                                                        AgentRunner.fetchTasks(goalId, RetrofitClient.instance) {
                                                            binding.tvAiContent.append("\n\n$it")
                                                        }
                                                    }
                                                }

                                                override fun onFailure(call: Call<GoalHistoryResponse>, t: Throwable) {}
                                            })
                                    }
                                }

                                override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {}
                            })

                        val data = response.body()?.data

                        val used = data?.used_amount ?: amount
                        val extra = data?.extra_amount ?: 0.0

                        val message = if (extra > 0) {
                            "Only ₹${used.toInt()} added. ₹${extra.toInt()} kept in account"
                        } else {
                            "Contribution added!"
                        }

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                        dialog.dismiss()

                        loadHistory()
                        loadGoalDetails()
                        // ✅ Get expected daily saving from insight
                        val expected = lastGoalInsight?.requiredPerDay ?: 0.0
                        val actual = amount

                        sendFeedback(
                            strategy = lastStrategy,
                            actual = actual,
                            expected = expected
                        )

                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GoalProgressResponse>, t: Throwable) {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun generateMultiGoalInsights(
        goals: List<Goal>,
        historyMap: Map<String, List<GoalProgress>>,
        transactions: List<TransactionItem>
    ) {
        val result = InsightEngine.generateMultiGoal(goals, historyMap, transactions)

        result.allocation.forEach { (goal, amount) ->
            android.util.Log.d("AI_ALLOC", "Goal -> ₹$amount/day")
        }
    }
    private fun sendFeedback(strategy: String, actual: Double, expected: Double) {

        val request = AgentFeedbackRequest(
            goal_id = goalId.toString(),
            strategy = strategy,
            actual_saved = actual,
            expected_saved = expected,
            success = actual >= expected
        )

        RetrofitClient.instance.sendFeedback(request)
            .enqueue(object : Callback<GenericResponse> {

                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {}

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {}
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}