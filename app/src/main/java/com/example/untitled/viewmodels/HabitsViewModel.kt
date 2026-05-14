package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.untitled.models.CheckHabitRequest
import com.example.untitled.models.CheckHabitResponse
import com.example.untitled.models.Habit
import com.example.untitled.models.HabitsResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _habits =
        MutableLiveData<List<Habit>>()

    val habits:
            LiveData<List<Habit>> = _habits

    private val _todayHabits =
        MutableLiveData<List<Habit>>()

    val todayHabits:
            LiveData<List<Habit>> =
        _todayHabits

    private val _loading =
        MutableLiveData<Boolean>()

    val loading:
            LiveData<Boolean> = _loading
    private val _completion =
        MutableLiveData<Int>()

    val completion:
            LiveData<Int> = _completion

    fun fetchHabits() {

        _loading.value = true

        RetrofitClient.instance
            .getHabits()
            .enqueue(object : Callback<HabitsResponse> {

                override fun onResponse(
                    call: Call<HabitsResponse>,
                    response: Response<HabitsResponse>
                ) {

                    _loading.value = false

                    if (
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        val habits =
                            response.body()?.data
                                ?: emptyList()

                        _habits.value =
                            habits

                        filterTodayHabits(
                            habits
                        )
                    }
                }

                override fun onFailure(
                    call: Call<HabitsResponse>,
                    t: Throwable
                ) {

                    _loading.value = false
                }
            })
    }

    private fun filterTodayHabits(
        habits: List<Habit>
    ) {

        val today =
            SimpleDateFormat(
                "EEE",
                Locale.getDefault()
            ).format(Date())

        val filtered =
            habits.filter { habit ->

                when (
                    habit.frequency.lowercase()
                ) {

                    "daily" -> true

                    "custom" -> {

                        habit.selected_days
                            ?.contains(today) == true
                    }

                    else -> false
                }
            }

                _todayHabits.value =
                    filtered

                val completed =
                    filtered.count {
                        it.completed_today == 1
                    }

                val percent =

                    if(filtered.isEmpty()) {
                        0
                    } else {

                        (completed * 100)/filtered.size
                    }

                _completion.value = percent
    }
    fun checkHabit(habitId: Int) {

        val date =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

        RetrofitClient.instance
            .checkHabit(
                CheckHabitRequest(
                    habit_id = habitId.toString(),
                    date = date
                )
            )
            .enqueue(object :
                Callback<CheckHabitResponse> {

                override fun onResponse(
                    call: Call<CheckHabitResponse>,
                    response: Response<CheckHabitResponse>
                ) {

                    fetchHabits()
                }

                override fun onFailure(
                    call: Call<CheckHabitResponse>,
                    t: Throwable
                ) {

                }
            })
    }
}