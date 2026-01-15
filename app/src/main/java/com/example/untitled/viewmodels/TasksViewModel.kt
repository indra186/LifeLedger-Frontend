package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.TaskEntity
import com.example.untitled.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    val tasks: StateFlow<List<TaskEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        
        tasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun updateTaskStatus(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, isCompleted)
        }
    }
}
