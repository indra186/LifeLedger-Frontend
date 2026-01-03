package com.example.untitled.network

import com.example.untitled.models.AddGoalRequest
import com.example.untitled.models.AddGoalResponse
import com.example.untitled.models.DashboardResponse
import com.example.untitled.models.GoalsResponse
import com.example.untitled.models.LoginRequest
import com.example.untitled.models.LoginResponse
import com.example.untitled.models.SendOtpRequest
import com.example.untitled.models.SendOtpResponse
import com.example.untitled.models.SignupRequest
import com.example.untitled.models.SignupResponse
import com.example.untitled.models.VerifyOtpRequest
import com.example.untitled.models.VerifyOtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/signup")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>

    @POST("send_otp.php")
    fun sendOtp(@Body request: SendOtpRequest): Call<SendOtpResponse>

    @POST("verify_otp.php")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<VerifyOtpResponse>

    @GET("dashboard.php")
    fun getDashboardData(@Query("user_id") userId: String): Call<DashboardResponse>

    @GET("goals_list.php")
    fun getGoals(@Query("user_id") userId: String): Call<GoalsResponse>

    @POST("goal_save.php")
    fun addGoal(@Body request: AddGoalRequest): Call<AddGoalResponse>
}
