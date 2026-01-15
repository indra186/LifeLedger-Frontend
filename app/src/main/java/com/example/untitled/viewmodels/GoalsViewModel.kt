package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.GoalEntity
import com.example.untitled.data.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GoalRepository

    val goals: StateFlow<List<GoalEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).goalDao()
        repository = GoalRepository(dao)

        goals = repository.allGoals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}
