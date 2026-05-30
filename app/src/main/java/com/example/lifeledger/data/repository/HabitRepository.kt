package com.example.lifeledger.data.repository

import com.example.lifeledger.data.local.dao.HabitDao
import com.example.lifeledger.data.local.entities.HabitEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()

    suspend fun insertHabit(habit: HabitEntity) {
        habitDao.insertHabit(habit)
    }
    fun getTodayHabits(today: String) =
        habitDao.getTodayHabits(today)
}
