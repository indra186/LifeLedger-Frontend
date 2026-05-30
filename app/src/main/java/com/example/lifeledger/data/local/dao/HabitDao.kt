package com.example.lifeledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lifeledger.data.local.entities.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Query("""
    SELECT * FROM habits
    WHERE
        frequency = 'daily'
        OR (
            frequency = 'custom'
            AND selectedDays LIKE '%' || :today || '%'
        )
""")
    fun getTodayHabits(
        today: String
    ): Flow<List<HabitEntity>>
}
