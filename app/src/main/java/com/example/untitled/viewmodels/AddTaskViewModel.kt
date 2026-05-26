package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.TaskEntity
import com.example.untitled.data.repository.TaskRepository
import com.example.untitled.models.CreateTaskRequest
import com.example.untitled.models.Task
import kotlinx.coroutines.launch
import com.example.untitled.network.RetrofitClient

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {
    
//    private val repository: TaskRepository
//

    private val _saveTaskState =
        MutableLiveData<UIState<Task>>()

    val saveTaskState:
            LiveData<UIState<Task>> = _saveTaskState

//    init {
//        val taskDao = AppDatabase.getDatabase(application).taskDao()
//        repository = TaskRepository(taskDao)
//    }

    fun saveTask(
        title: String,
        description: String,
        date: String,
        time: String,
        repeatType: String,
        repeatDays: String,
        priority: String,
        reminderEnabled: Boolean,
        attachmentUri: String?
    ) {

        if(title.isBlank()) {

            _saveTaskState.value =
                UIState.Error(
                    "Task title is required"
                )

            return
        }

        if(date.isBlank()) {

            _saveTaskState.value =
                UIState.Error(
                    "Please select a date"
                )

            return
        }

        if(time.isBlank()) {

            _saveTaskState.value =
                UIState.Error(
                    "Please select time"
                )

            return
        }

        if(
            repeatType == "weekly" &&
            repeatDays.isBlank()
        ) {

            _saveTaskState.value =
                UIState.Error(
                    "Select at least one weekday"
                )

            return
        }

        if(
            priority != "low" &&
            priority != "medium" &&
            priority != "high"
        ) {

            _saveTaskState.value =
                UIState.Error(
                    "Invalid priority"
                )

            return
        }

        _saveTaskState.value =
            UIState.Loading

        viewModelScope.launch {

            try {

                val request =
                    CreateTaskRequest(

                        title = title,

                        description = description,

                        date = date,

                        time = time,

                        priority = priority,

                        repeat_type = repeatType,

                        repeat_days = repeatDays,

                        reminder_enabled =
                            reminderEnabled,

                        attachment_uri =
                            attachmentUri
                    )

                val response =
                    RetrofitClient
                        .instance
                        .createTask(request)

                if(response.success) {

                    _saveTaskState.value =
                        UIState.Success(
                            response.data!!
                        )

                } else {

                    _saveTaskState.value =
                        UIState.Error(response.message)
                }

//                if(
//                    response.isSuccessful &&
//                    response.body()?.success == true
//                ) {
//
////                    val task =
////                        TaskEntity(
////                            title = title,
////                            description = description,
////                            date = date,
////                            time = time,
////                            repeatType = repeatType,
////                            repeatDays = repeatDays,
////                            priority = priority,
////                            reminderEnabled = reminderEnabled,
////                            attachmentUri = attachmentUri
////                        )
////
////                    repository.insertTask(task)
//
//                    _saveTaskState.value =
//                        UIState.Success(Unit)
//
//                } else {
//
//                    _saveTaskState.value =
//                        UIState.Error(
//                            response.body()?.message
//                                ?: "Task creation failed"
//                        )
//                }

            } catch(e: Exception) {

                _saveTaskState.value =
                    UIState.Error(
                        e.message ?: "Error"
                    )
            }
        }
    }
}
