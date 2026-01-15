package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.TaskEntity
import com.example.untitled.data.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TaskRepository
    
    private val _saveTaskState = MutableLiveData<UIState<Unit>>()
    val saveTaskState: LiveData<UIState<Unit>> = _saveTaskState

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
    }

    fun saveTask(title: String, date: String, time: String, category: String) {
        if (title.isBlank()) {
            _saveTaskState.value = UIState.Error("Title cannot be empty")
            return
        }

        _saveTaskState.value = UIState.Loading

        viewModelScope.launch {
            try {
                val task = TaskEntity(
                    title = title,
                    date = if (date.contains("-")) date else null,
                    time = if (time.contains(":")) time else null,
                    category = category,
                    isCompleted = false
                )
                repository.insertTask(task)
                _saveTaskState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _saveTaskState.value = UIState.Error("Failed to save task: ${e.message}")
            }
        }
    }
}
