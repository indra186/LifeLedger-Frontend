package com.example.untitled.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.untitled.data.local.dao.*
import com.example.untitled.data.local.entities.*

@Database(
    entities = [
        TaskEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        TransactionEntity::class,
        HealthMetricEntity::class,
        HabitEntity::class,
        AccountEntity::class,
        JournalEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao
    abstract fun transactionDao(): TransactionDao
    abstract fun healthMetricDao(): HealthMetricDao
    abstract fun habitDao(): HabitDao
    abstract fun accountDao(): AccountDao
    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "life_ledger_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
