package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.HabitEntity
import com.example.untitled.data.repository.HabitRepository
import com.example.untitled.models.CreateHabitRequest
import com.example.untitled.models.CreateHabitResponse
import com.example.untitled.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateHabitViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: HabitRepository

    private val _saveState =
        MutableLiveData<UIState<Unit>>()

    val saveState:
            LiveData<UIState<Unit>> = _saveState

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .habitDao()

        repository =
            HabitRepository(dao)
    }

    fun saveHabit(
        title: String,
        description: String,
        frequency: String,
        selectedDays: String,
        goal: Int,
        unit: String,
        reminder: String?,
        icon: String
    ) {

        if(title.isBlank()) {

            _saveState.value =
                UIState.Error(
                    "Habit title is required"
                )

            return
        }

        _saveState.value =
            UIState.Loading

        try{
            val request = CreateHabitRequest( habit_name = title, description = description, icon = icon, frequency = frequency, selected_days = selectedDays, goal_per_day = goal, goal_unit = unit, reminder_time = reminder )
            RetrofitClient.instance
            .createHabit(request)
            .enqueue(object : Callback<CreateHabitResponse> {

                override fun onResponse(
                    call: Call<CreateHabitResponse>,
                    response: Response<CreateHabitResponse>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        _saveState.value =
                            UIState.Success(Unit)

                    } else {

                        _saveState.value =
                            UIState.Error(
                                response.body()?.message
                                    ?: "Failed"
                            )
                    }
                }

                override fun onFailure(
                    call: Call<CreateHabitResponse>,
                    t: Throwable
                ) {

                    _saveState.value =
                        UIState.Error(
                            t.message ?: "Network Error"
                        )
                }
            })}
        catch (e: Exception) {

                _saveState.value =
                    UIState.Error(e.message ?: "Error")
            }

    }
}