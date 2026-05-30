package com.example.lifeledger.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lifeledger.models.TransactionItem
import com.example.lifeledger.models.TransactionsResponse
import com.example.lifeledger.network.RetrofitClient
import com.example.lifeledger.utils.MonthUtils
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

        val calendar = java.util.Calendar.getInstance()

        val month = calendar.get(java.util.Calendar.MONTH)
        val year = calendar.get(java.util.Calendar.YEAR)

        val (startDate, endDate) =
            MonthUtils.getMonthDateRange(month, year)

        RetrofitClient.instance.getTransactionsByMonth(
            userId,
            startDate,
            endDate
        ).enqueue(object : Callback<TransactionsResponse> {

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
