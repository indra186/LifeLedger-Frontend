package com.example.untitled.viewmodels

import androidx.lifecycle.ViewModel
import com.example.untitled.models.Account
import com.example.untitled.models.AccountsResponse
import com.example.untitled.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountsViewModel : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        RetrofitClient.instance.getAccounts()
            .enqueue(object : Callback<AccountsResponse> {
                override fun onResponse(
                    call: Call<AccountsResponse>,
                    response: Response<AccountsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        _accounts.value = response.body()?.data ?: emptyList()
                    }
                }

                override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                    // ignore
                }
            })
    }
}
