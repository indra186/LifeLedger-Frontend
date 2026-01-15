package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.GoalEntity
import com.example.untitled.data.repository.GoalRepository
import kotlinx.coroutines.launch

class AddGoalViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: GoalRepository
    
    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    init {
        val dao = AppDatabase.getDatabase(application).goalDao()
        repository = GoalRepository(dao)
    }

    fun saveGoal(title: String, targetAmount: Double, deadline: String) {
        if (title.isBlank()) {
            _saveState.value = UIState.Error("Goal title is required")
            return
        }
        if (targetAmount <= 0) {
            _saveState.value = UIState.Error("Target amount must be greater than 0")
            return
        }

        _saveState.value = UIState.Loading

        viewModelScope.launch {
            try {
                val goal = GoalEntity(
                    title = title,
                    targetAmount = targetAmount,
                    deadline = if (deadline.contains("-")) deadline else null,
                    currentAmount = 0.0
                )
                repository.insertGoal(goal)
                _saveState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UIState.Error("Failed to save goal: ${e.message}")
            }
        }
    }
}
