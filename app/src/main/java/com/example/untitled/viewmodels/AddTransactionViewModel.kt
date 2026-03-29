package com.example.untitled.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.untitled.data.local.AppDatabase
import com.example.untitled.data.local.entities.TransactionEntity
import com.example.untitled.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TransactionRepository
    
    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    fun saveTransaction(title: String, amount: Double, type: String, category: String, date: String, notes: String?) {
        if (amount <= 0) {
            _saveState.value = UIState.Error("Amount must be greater than 0")
            return
        }
        if (title.isBlank()) {
            _saveState.value = UIState.Error("Title or Description is required")
            return
        }

        _saveState.value = UIState.Loading

        viewModelScope.launch {
            try {
                val transaction = TransactionEntity(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    date = date,
                    notes = notes
                )
                repository.insertTransaction(transaction)
                _saveState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = UIState.Error("Failed to save transaction: ${e.message}")
            }
        }
    }
}
