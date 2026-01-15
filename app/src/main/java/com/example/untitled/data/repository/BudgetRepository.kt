package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.BudgetDao
import com.example.untitled.data.local.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }
}
