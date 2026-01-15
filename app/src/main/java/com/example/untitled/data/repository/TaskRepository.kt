package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.TaskDao
import com.example.untitled.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }
    
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean) {
        taskDao.updateTaskStatus(id, isCompleted)
    }
}
