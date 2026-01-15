package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.HabitEntity
import com.example.untitled.data.repository.HabitRepository
import kotlinx.coroutines.launch

class CreateHabitViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: HabitRepository
    
    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    init {
        val dao = AppDatabase.getDatabase(application).habitDao()
        repository = HabitRepository(dao)
    }

    fun saveHabit(title: String, frequency: String) {
        if (title.isBlank()) {
            _saveState.value = UIState.Error("Habit title is required")
            return
        }

        _saveState.value = UIState.Loading

        viewModelScope.launch {
            try {
                val habit = HabitEntity(
                    title = title,
                    frequency = frequency
                )
                repository.insertHabit(habit)
                _saveState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UIState.Error("Failed to save habit: ${e.message}")
            }
        }
    }
}
