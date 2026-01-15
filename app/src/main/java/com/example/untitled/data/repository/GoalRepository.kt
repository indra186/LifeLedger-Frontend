package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.GoalDao
import com.example.untitled.data.local.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()

    suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }
}
