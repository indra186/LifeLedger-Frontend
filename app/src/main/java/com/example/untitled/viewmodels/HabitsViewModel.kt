package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.HabitEntity
import com.example.untitled.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HabitsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository

    val habits: StateFlow<List<HabitEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).habitDao()
        repository = HabitRepository(dao)

        habits = repository.allHabits.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}
