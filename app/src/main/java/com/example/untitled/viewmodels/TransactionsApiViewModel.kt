package com.example.untitled.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.untitled.models.TransactionItem
import com.example.untitled.models.TransactionsResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsApiViewModel(application: Application) : AndroidViewModel(application) {

    private val _transactions = MutableLiveData<List<TransactionItem>>()
    val transactions: LiveData<List<TransactionItem>> = _transactions

    fun loadTransactions() {

        val prefs = getApplication<Application>()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getString("userId", null)

        if (userId == null) {
            _transactions.value = emptyList()
            return
        }

        RetrofitClient.instance.getTransactions(userId)
            .enqueue(object : Callback<TransactionsResponse> {

                override fun onResponse(
                    call: Call<TransactionsResponse>,
                    response: Response<TransactionsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        _transactions.value = response.body()?.data ?: emptyList()
                    } else {
                        _transactions.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
                    _transactions.value = emptyList()
                }
            })
    }
}
