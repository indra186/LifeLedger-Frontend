package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.HealthMetricDao
import com.example.untitled.data.local.entities.HealthMetricEntity
import kotlinx.coroutines.flow.Flow

class HealthRepository(private val healthMetricDao: HealthMetricDao) {
    val allMetrics: Flow<List<HealthMetricEntity>> = healthMetricDao.getAllMetrics()

    suspend fun insertMetric(metric: HealthMetricEntity) {
        healthMetricDao.insertMetric(metric)
    }
}
