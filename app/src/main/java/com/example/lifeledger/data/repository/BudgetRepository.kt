package com.example.lifeledger.data.repository

import com.example.lifeledger.data.local.dao.BudgetDao
import com.example.lifeledger.data.local.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }
}
