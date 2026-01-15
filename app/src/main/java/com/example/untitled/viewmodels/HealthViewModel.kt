package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.HealthMetricEntity
import com.example.untitled.data.repository.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HealthRepository

    val metrics: StateFlow<List<HealthMetricEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).healthMetricDao()
        repository = HealthRepository(dao)

        metrics = repository.allMetrics.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}
