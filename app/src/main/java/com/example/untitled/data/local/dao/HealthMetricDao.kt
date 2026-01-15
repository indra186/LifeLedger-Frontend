package com.example.untitled.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.untitled.data.local.entities.HealthMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMetricDao {
    @Query("SELECT * FROM health_metrics ORDER BY date DESC, time DESC")
    fun getAllMetrics(): Flow<List<HealthMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: HealthMetricEntity): Long
}
