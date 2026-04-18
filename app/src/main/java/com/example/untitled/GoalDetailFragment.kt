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
    private var currentGoal: Goal? = null

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
            checkDataAndOptimize()
        }

    }
    private fun getUserId(): String {
        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)

        return prefs.getString("userId", "") ?: ""
    }
    private fun calculateGoalBasedSaving(history: List<GoalProgress>): Double {

        if (history.isEmpty()) return 0.0

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

        return try {
            val sorted = history.sortedBy { it.date_added }

            val first = sdf.parse(sorted.first().date_added)
            val last = sdf.parse(sorted.last().date_added)

            val diff = last.time - first.time
            val days = diff / (1000 * 60 * 60 * 24)

            if (days <= 0) return 0.0

            val total = history.sumOf { it.amount_added }

            (total / days) * 30 // monthly avg

        } catch (e: Exception) {
            0.0
        }
    }
    private fun checkDataAndOptimize() {

        RetrofitClient.instance.getTransactions(getUserId())
            .enqueue(object : Callback<TransactionsResponse> {

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
        val remaining = goal.target_amount - goal.current_amount

        if (remaining <= 0) {
            showResultDialogCompleted(goal)
            return
        }

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

        val daysLeft: Int = if (goal.deadline != null) {
            try {
                val deadlineDate = sdf.parse(goal.deadline)!!
                val now = java.util.Date()
                val diff = deadlineDate.time - now.time
                (diff / (1000 * 60 * 60 * 24)).toInt()
            } catch (e: Exception) {
                -1
            }
        } else -1
// ✅ 🔥 ADD HERE (DEADLINE PASSED LOGIC)
        if (daysLeft < 0) {

            val dailyActual = calculateDailySaving(history)
            val delayDays = kotlin.math.abs(daysLeft)

            val recoveryDays = when {
                dailyActual <= 0 -> 30
                delayDays <= 3 -> 7
                delayDays <= 10 -> 14
                else -> (remaining / dailyActual).toInt().coerceIn(7, 30)
            }

            val dailyNeeded = remaining / recoveryDays
            val gap = dailyNeeded - dailyActual

            val message = if (dailyActual >= dailyNeeded) {
                """
❌ Deadline Passed

Remaining: ₹${remaining.toInt()}

💡 Recovery Plan:
You can recover in $recoveryDays days

👉 Save ₹${dailyNeeded.toInt()}/day
"""
            } else {
                """
❌ Deadline Passed

Remaining: ₹${remaining.toInt()}

🚨 Recovery Plan ($recoveryDays days):

Required: ₹${dailyNeeded.toInt()}/day  
You save: ₹${dailyActual.toInt()}/day  

⚠ Short by ₹${gap.toInt()}/day  

👉 Suggestions:
• Extend deadline  
• Reduce expenses  
• Increase income
"""
            }

            binding.tvAiContent.text = message.trimIndent()
            return
        }
        // 🔥 🚨 STEP 0: SHORT TERM (WEEKEND / <=7 DAYS)
        if (daysLeft in 1..7) {

            val dailyRequired = remaining / daysLeft
            val dailyActual = calculateDailySaving(history)

            val message = when {
                dailyActual >= dailyRequired -> {
                    """
🎯 Short-Term Goal

Required: ₹${dailyRequired.toInt()}/day  
You are saving: ₹${dailyActual.toInt()}/day  

✅ You can achieve this goal
"""
                }
                else -> {
                    val gap = (dailyRequired - dailyActual)

                    """
🚨 Short-Term Goal

Required: ₹${dailyRequired.toInt()}/day  
You are saving: ₹${dailyActual.toInt()}/day  

⚠ You may fall short by ₹${gap.toInt()}/day  
👉 Try reducing expenses or extend deadline
"""
                }
            }

            binding.tvAiContent.text = message.trimIndent()
            return
        }

        // 🔥 NORMAL LOGIC CONTINUES BELOW

        val isShortTerm = daysLeft in 1..30

        val requiredSaving: Double? = if (daysLeft > 0) {
            when {
                daysLeft <= 30 -> remaining / daysLeft
                else -> {
                    val monthsLeft = daysLeft / 30.0
                    remaining / monthsLeft
                }
            }
        } else null

        val transactionSaving = calculateMonthlySavings(transactions)

        val goalSaving = if (isShortTerm) {
            calculateDailySaving(history)
        } else {
            calculateGoalBasedSaving(history)
        }

        val actualSaving = when {
            goalSaving > 0 -> goalSaving
            transactionSaving > 0 -> transactionSaving
            else -> totalBalance * 0.3
        }

        val realSaving = when {
            daysLeft in 0..3 && requiredSaving != null -> requiredSaving
            requiredSaving != null && actualSaving > 0 -> minOf(actualSaving, requiredSaving)
            actualSaving > 0 -> actualSaving
            else -> 0.0
        }

        val months = if (realSaving > 0) (remaining / realSaving) else -1.0
        val days = (months * 30).toInt()

        val confidence = getConfidence(transactions)
        val lastDate = transactions.maxByOrNull { it.tx_date }?.tx_date
        val lastUpdated = getLastUpdatedText(lastDate)

        showResultDialog(
            goal.title,
            months,
            days,
            realSaving,
            confidence,
            lastUpdated,
            goal.deadline
        )
    }
    private fun calculateDailySaving(history: List<GoalProgress>): Double {

        if (history.isEmpty()) return 0.0

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

        return try {
            val sorted = history.sortedBy { it.date_added }

            val first = sdf.parse(sorted.first().date_added)
            val last = sdf.parse(sorted.last().date_added)

            val diff = last.time - first.time
            val days = (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(1)

            val total = history.sumOf { it.amount_added }

            total / days   // DAILY average
        } catch (e: Exception) {
            0.0
        }
    }
    private fun showResultDialogCompleted(goal: Goal) {

        binding.tvAiContent.text = """
🎉 Goal Completed!

You have successfully reached your goal: ${goal.title}

You're financially on track 🚀
""".trimIndent()
    }
    private fun calculateMonthlySavings(list: List<TransactionItem>): Double {

        if (list.isEmpty()) return 0.0

        val income = list
            .filter { it.type == "income" }
            .sumOf { it.amount }

        val expense = list
            .filter { it.type == "expense" }
            .sumOf { it.amount }

        val net = income - expense

        // Assume data spans 1 month (simple version)
        return if (net > 0) net else 0.0
    }
    private fun getConfidence(list: List<TransactionItem>): String {

        val lastDateStr = list.maxByOrNull { it.tx_date }?.tx_date ?: return "LOW"

        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

            val lastDate = sdf.parse(lastDateStr) ?: return "LOW"
            val now = java.util.Date()

            val diff = now.time - lastDate.time
            val days = diff / (1000 * 60 * 60 * 24)

            when {
                days <= 1 -> "HIGH"
                days <= 3 -> "MEDIUM"
                else -> "LOW"
            }

        } catch (e: Exception) {
            "LOW"
        }
    }
    private fun showResultDialog(
        title: String,
        months: Double,
        days: Int,
        monthlySaving: Double,
        confidence: String,
        lastUpdated: String,
        deadline: String?
    ) {
        val timeText = when {
            months == 0.0 -> "Goal achieved 🎉"
            months < 0 -> "Not enough data"
            months < 1 -> "$days days"
            else -> "${"%.1f".format(months)} months"
        }
        val deadlineMessage = if (deadline != null) {
            try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val deadlineDate = sdf.parse(deadline)!!
                val now = java.util.Date()

                val diff = deadlineDate.time - now.time
                val daysLeft = (diff / (1000 * 60 * 60 * 24)).toInt()

                when {
                    daysLeft < 0 -> "❌ Deadline passed! Adjust your goal"
                    daysLeft == 0 -> "⚠ Deadline is TODAY"
                    daysLeft in 1..3 -> "🚨 Urgent: Only $daysLeft days left"
                    daysLeft in 4..7 -> "⚠ Only $daysLeft days left"
                    daysLeft < days -> "⚠ You may miss your deadline"
                    else -> "✅ On track"
                }

            } catch (e: Exception) {
                ""
            }
        } else ""
        val savingText = when {
            deadline != null && days <= 30 -> {
                "₹${monthlySaving.toInt()}/day"
            }
            else -> {
                "₹${monthlySaving.toInt()}/month"
            }
        }

        binding.tvAiContent.text = """
📊 Goal Insight: $title

Recommended saving: $savingText

Time to goal: $timeText

$deadlineMessage

Confidence: $confidence
Last updated: $lastUpdated
""".trimIndent()
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

//                        btnAdd.setOnClickListener {
//
//                            val amount = etAmount.text.toString().toDoubleOrNull()
//
//                            if (amount == null || amount <= 0) {
//                                Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
//                                return@setOnClickListener
//                            }
//
//                            val selectedAccount = accounts[spinner.selectedItemPosition]
//
//                            addContribution(amount, selectedAccount.id, dialog)
//                        }
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

                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GoalProgressResponse>, t: Throwable) {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}