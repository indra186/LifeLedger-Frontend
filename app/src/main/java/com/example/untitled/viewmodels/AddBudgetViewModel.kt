package com.example.untitled.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.untitled.models.CreateBudgetRequest
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBudgetViewModel : ViewModel() {

    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    fun saveBudget(category: String, amount: Double) {
        if (amount <= 0) {
            _saveState.value = UIState.Error("Amount must be greater than 0")
            return
        }

        _saveState.value = UIState.Loading

        val request = CreateBudgetRequest(
            category = category,
            limit_amount = amount,
            period = "monthly"
        )

        RetrofitClient.instance.createBudget(request)
            .enqueue(object : Callback<com.example.untitled.models.CreateBudgetResponse> {
                override fun onResponse(
                    call: Call<com.example.untitled.models.CreateBudgetResponse>,
                    response: Response<com.example.untitled.models.CreateBudgetResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        _saveState.value = UIState.Success(Unit)
                    } else {
                        _saveState.value = UIState.Error("Failed to save budget")
                    }
                }

                override fun onFailure(call: Call<com.example.untitled.models.CreateBudgetResponse>, t: Throwable) {
                    _saveState.value = UIState.Error(t.message ?: "Network error")
                }
            })
    }
}
