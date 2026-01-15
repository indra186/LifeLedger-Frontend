package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.HabitDao
import com.example.untitled.data.local.entities.HabitEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()

    suspend fun insertHabit(habit: HabitEntity) {
        habitDao.insertHabit(habit)
    }
}
