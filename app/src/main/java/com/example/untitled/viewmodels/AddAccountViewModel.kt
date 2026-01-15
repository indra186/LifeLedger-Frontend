package com.example.untitled.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.untitled.models.AddAccountRequest
import com.example.untitled.models.AddAccountResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAccountViewModel : ViewModel() {

    private val _saveState = MutableLiveData<UIState<Unit>>()
    val saveState: LiveData<UIState<Unit>> = _saveState

    fun saveAccount(name: String, type: String, balance: Double) {
        _saveState.value = UIState.Loading

        val request = AddAccountRequest(name, type, balance)

        RetrofitClient.instance.addAccount(request)
            .enqueue(object : Callback<AddAccountResponse> {

                override fun onResponse(
                    call: Call<AddAccountResponse>,
                    response: Response<AddAccountResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        _saveState.postValue(UIState.Success(Unit))
                    } else {
                        _saveState.postValue(UIState.Error("Failed to save account"))
                    }
                }

                override fun onFailure(call: Call<AddAccountResponse>, t: Throwable) {
                    _saveState.postValue(UIState.Error(t.message ?: "Network error"))
                }
            })
    }
}
