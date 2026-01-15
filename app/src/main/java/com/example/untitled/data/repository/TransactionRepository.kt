package com.example.untitled.data.repository

import com.example.untitled.data.local.dao.TransactionDao
import com.example.untitled.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
}
