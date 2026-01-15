package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.TransactionEntity
import com.example.untitled.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository

    val transactions: StateFlow<List<TransactionEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)

        transactions = repository.allTransactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}
