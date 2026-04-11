package com.example.untitled.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.untitled.models.AddGoalRequest
import com.example.untitled.models.AddGoalResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddGoalViewModel(application: Application) : AndroidViewModel(application) {

    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    fun saveGoal(title: String, targetAmount: Double, intialAmount: Double, deadline: String) {

        if (title.isBlank()) {
            _saveState.value = UIState.Error("Goal title is required")
            return
        }

        if (targetAmount <= 0) {
            _saveState.value = UIState.Error("Target amount must be greater than 0")
            return
        }

        // GET USER ID (same as Transactions)
        val prefs = getApplication<Application>()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getString("userId", null)

        if (userId == null) {
            _saveState.value = UIState.Error("User not logged in")
            return
        }

        _saveState.value = UIState.Loading

        val request = AddGoalRequest(
            user_id = userId,
            title = title,
            target_amount = targetAmount,
            deadline = deadline,
            current_amount = intialAmount
        )

        RetrofitClient.instance.addGoal(request)
            .enqueue(object : Callback<AddGoalResponse> {

                override fun onResponse(
                    call: Call<AddGoalResponse>,
                    response: Response<AddGoalResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        _saveState.value = UIState.Success(Unit)
                    } else {
                        _saveState.value = UIState.Error("Failed to save goal")
                    }
                }

                override fun onFailure(call: Call<AddGoalResponse>, t: Throwable) {
                    _saveState.value = UIState.Error("Network error")
                }
            })
    }
}