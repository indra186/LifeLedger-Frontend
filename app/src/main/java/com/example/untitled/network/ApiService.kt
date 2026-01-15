package com.example.untitled.network

import com.example.untitled.models.*
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    
    // Auth
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<ResponseBody> 

    @POST("register.php")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>

    @POST("send_otp.php")
    fun sendOtp(@Body request: SendOtpRequest): Call<SendOtpResponse>

    @POST("verify_otp.php")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<VerifyOtpResponse>

    // Dashboard
    @GET("dashboard.php")
    fun getDashboardData(): Call<DashboardResponse>


    // Goals
    @GET("goals_list.php")
    fun getGoals(@Query("user_id") userId: String): Call<GoalsResponse>

    @POST("goal_save.php")
    fun addGoal(@Body request: AddGoalRequest): Call<AddGoalResponse>

    // Tasks
    @GET("tasks_list.php")
    fun getTasks(@Query("user_id") userId: String): Call<TasksResponse>

    @POST("task_save.php")
    fun addTask(@Body request: AddTaskRequest): Call<AddTaskResponse>

    // Habits
    @GET("habits_list.php")
    fun getHabits(@Query("user_id") userId: String): Call<HabitsResponse>

    @POST("habit_save.php")
    fun createHabit(@Body request: CreateHabitRequest): Call<CreateHabitResponse>

    @POST("habit_check.php")
    fun checkHabit(@Body request: CheckHabitRequest): Call<CheckHabitResponse>

    // Budgets
    @GET("budgets_list.php")
    fun getBudgets(): Call<BudgetsResponse>
//    @GET("budgets_list.php")
//    fun getBudgets(): Call<ResponseBody>


    @POST("budget_save.php")
    fun createBudget(@Body request: CreateBudgetRequest): Call<CreateBudgetResponse>

    // Transactions
    @GET("transactions_list.php")
    fun getTransactions(@Query("user_id") userId: String): Call<TransactionsResponse>

    @POST("transaction_save.php")
    fun addTransaction(@Body request: AddTransactionRequest): Call<AddTransactionResponse>

    // Health - Add this if you want to sync health data to server later
    /*
    @GET("health_list.php")
    fun getHealthMetrics(@Query("user_id") userId: String): Call<HealthMetricsResponse>

    @POST("health_save.php")
    fun addHealthMetric(@Body request: AddHealthRequest): Call<AddHealthResponse>
    */
    @GET("accounts_list.php")
    fun getAccounts(): Call<AccountsResponse>

    @POST("account_add.php")
    fun addAccount(@Body request: AddAccountRequest): Call<AddAccountResponse>


}
