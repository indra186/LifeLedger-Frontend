package com.example.untitled.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.untitled.models.CreateTaskResponse
import com.example.untitled.models.DeleteTaskRequest
import com.example.untitled.models.Task
import com.example.untitled.models.TaskSection
import com.example.untitled.models.TasksResponse
import com.example.untitled.models.UpdateTaskStatusRequest
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TasksViewModel : ViewModel() {

    private val _sections =
        MutableLiveData<List<TaskSection>>()

    val sections:
            LiveData<List<TaskSection>>
            = _sections

    private val _progress =
        MutableLiveData<Int>()

    val progress:
            LiveData<Int>
            = _progress

    fun fetchTasks() {

        RetrofitClient
            .instance
            .getTasks()
            .enqueue(

                object : Callback<TasksResponse> {

                    override fun onResponse(
                        call: Call<TasksResponse>,
                        response: Response<TasksResponse>
                    ) {

                        if(
                            response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            val tasks =
                                response.body()?.data
                                    ?: emptyList()
                            scheduleTaskReminders(tasks)
                            scheduleOverdueChecks(tasks)
                            createSections(tasks)
                        }
                    }

                    override fun onFailure(
                        call: Call<TasksResponse>,
                        t: Throwable
                    ) {

                        t.printStackTrace()
                    }
                }
            )
    }

    private fun createSections(
        tasks: List<Task>
    ) {

        val allTodayTasks =
            tasks.filter {
                shouldAppearToday(it)
            }

        val pendingTodayTasks =
            allTodayTasks.filter {
                it.completed == 0
            }

        val completedTodayTasks =
            allTodayTasks.filter {
                it.completed == 1
            }

        val upcomingTasks =
            tasks.filter {

                !shouldAppearToday(it) &&
                        it.completed == 0
            }

        val completedTasks =
            tasks.filter {
                it.completed == 1
            }

        val todayTitle =

            when {

                allTodayTasks.isEmpty() ->

                    "No tasks scheduled for Today"

                pendingTodayTasks.isEmpty() ->

                    "Today • Nice work! All tasks completed"

                else ->

                    "Today (${pendingTodayTasks.size})"
            }

        val sections =
            listOf(

                TaskSection(
                    todayTitle,
                    pendingTodayTasks
                ),

                TaskSection(

                    if(upcomingTasks.isEmpty())
                        "Upcoming • No tasks scheduled"
                    else
                        "Upcoming (${upcomingTasks.size})",

                    upcomingTasks
                ),

                TaskSection(
                    "Completed (${completedTasks.size})",
                    completedTasks
                )
            )

        _sections.value = sections

        calculateProgress(
            allTodayTasks
        )
    }
    private fun calculateProgress(
        todayTasks: List<Task>
    ) {

        if(todayTasks.isEmpty()) {

            _progress.value = 100

            return
        }

        val completed =
            todayTasks.count {
                it.completed == 1
            }

        val progress =
            (
                    completed.toFloat() /
                            todayTasks.size.toFloat()
                    ) * 100

        _progress.value =
            progress.toInt()
    }

//    private fun calculateProgress(
//        tasks: List<Task>
//    ) {
//
//        if(tasks.isEmpty()) {
//
//            _progress.value = 0
//
//            return
//        }
//
//        val completed =
//            tasks.count {
//
//                it.completed == 1
//            }
//
//        val progress =
//            (
//                    completed.toFloat() /
//                            tasks.size.toFloat()
//                    ) * 100
//
//        _progress.value =
//            progress.toInt()
//    }

    fun updateTaskStatus(
        task: Task,
        completed: Boolean
    ) {

        val request =
            UpdateTaskStatusRequest(

                task_id = task.id,

                completed =
                    if(completed) 1 else 0
            )

        RetrofitClient
            .instance
            .updateTaskStatus(request)
            .enqueue(

                object : Callback<com.example.untitled.models.CreateTaskResponse> {

                    override fun onResponse(
                        call: Call<com.example.untitled.models.CreateTaskResponse>,
                        response: Response<com.example.untitled.models.CreateTaskResponse>
                    ) {

                        fetchTasks()
                    }

                    override fun onFailure(
                        call: Call<com.example.untitled.models.CreateTaskResponse>,
                        t: Throwable
                    ) {

                        t.printStackTrace()
                    }
                }
            )
    }
    fun deleteTask(
        task: Task
    ) {

        val request =
            DeleteTaskRequest(
                task.id
            )

        RetrofitClient
            .instance
            .deleteTask(request)
            .enqueue(

                object :
                    Callback<CreateTaskResponse> {

                    override fun onResponse(
                        call: Call<CreateTaskResponse>,
                        response: Response<CreateTaskResponse>
                    ) {

                        fetchTasks()
                    }

                    override fun onFailure(
                        call: Call<CreateTaskResponse>,
                        t: Throwable
                    ) {

                        t.printStackTrace()
                    }
                }
            )
    }
    private fun shouldAppearToday(
        task: Task
    ): Boolean {

        val today =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

        val todayDay =
            SimpleDateFormat(
                "EEE",
                Locale.getDefault()
            ).format(Date())

        return when(task.repeat_type) {

            "daily" -> true

            "weekly" -> {

                task.repeat_days
                    ?.contains(todayDay, true) == true
            }

            "monthly" -> {

                val taskDay =
                    task.date
                        ?.split("-")
                        ?.getOrNull(2)

                val todayDate =
                    today
                        .split("-")
                        .getOrNull(2)

                taskDay == todayDate
            }

            else -> {

                task.date == today
            }
        }
    }
    private fun scheduleTaskReminders(
        tasks: List<Task>
    ) {

        val context =
            com.example.untitled.MyApplication.instance

        val formatter =
            java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
            )

        tasks.forEach { task ->

            if(
                task.reminder_enabled != 1 ||
                task.completed == 1
            ) {
                return@forEach
            }

            if(
                task.date.isNullOrEmpty() ||
                task.time.isNullOrEmpty()
            ) {
                return@forEach
            }

            try {

                val dateTime =
                    "${task.date} ${task.time}"

//                val triggerMillis =
//                    formatter.parse(dateTime)?.time
//                        ?: return@forEach
//
//                com.example.untitled.utils
//                    .ReminderScheduler
//                    .scheduleReminder(
//
//                        context,
//
//                        "Task Reminder",
//
//                        task.title,
//
//                        triggerMillis
//                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
    private fun scheduleOverdueChecks(
        tasks: List<Task>
    ) {

        val context =
            com.example.untitled.MyApplication.instance

        tasks.forEach { task ->

            com.example.untitled.utils
                .OverdueTaskScheduler
                .scheduleOverdueReminder(

                    context,
                    task
                )
        }
    }


}