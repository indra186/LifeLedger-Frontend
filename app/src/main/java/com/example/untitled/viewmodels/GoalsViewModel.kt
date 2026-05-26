package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.GoalEntity
import com.example.untitled.data.repository.GoalRepository
import com.example.untitled.network.RetrofitClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.untitled.models.Goal
import com.example.untitled.models.GoalsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: MutableStateFlow<List<Goal>> = _goals

    fun loadGoals() {

        val prefs = getApplication<Application>()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getString("userId", null) ?: return

        RetrofitClient.instance.getGoals(userId)
            .enqueue(object : Callback<GoalsResponse> {

                override fun onResponse(
                    call: Call<GoalsResponse>,
                    response: Response<GoalsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        _goals.value = response.body()?.data?.goals ?: emptyList()
                    } else {
                        _goals.value = emptyList()
                    }
                    Log.d("GOALS_API", _goals.value.toString())
                }

                override fun onFailure(call: Call<GoalsResponse>, t: Throwable) {
                    _goals.value = emptyList()
                }
            })
    }
}
