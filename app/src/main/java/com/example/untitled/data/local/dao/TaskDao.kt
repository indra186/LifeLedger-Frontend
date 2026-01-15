package com.example.untitled.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.untitled.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean)
}
