package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.HealthMetricEntity
import com.example.untitled.data.repository.HealthRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class AddHealthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: HealthRepository
    
    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    init {
        val dao = AppDatabase.getDatabase(application).healthMetricDao()
        repository = HealthRepository(dao)
    }

    fun saveMetric(type: String, value: Double, unit: String) {
        if (value <= 0) {
            _saveState.value = UIState.Error("Value must be greater than 0")
            return
        }

        _saveState.value = UIState.Loading

        viewModelScope.launch {
            try {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                val metric = HealthMetricEntity(
                    type = type,
                    value = value,
                    unit = unit,
                    date = currentDate,
                    time = currentTime
                )
                repository.insertMetric(metric)
                _saveState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UIState.Error("Failed to save metric: ${e.message}")
            }
        }
    }
}
