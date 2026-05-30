package com.example.lifeledger.viewmodels

//class BudgetsViewModel : ViewModel() {
//
//    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
//    val budgets: StateFlow<List<Budget>> = _budgets
//
//    fun loadBudgets() {
//        Log.d("BUDGET_API", "Calling budgets_list.php")
//
//        RetrofitClient.instance.getBudgetsByMonth(null,null)
//            .enqueue(object : Callback<BudgetsResponse> {
//
//                override fun onResponse(
//                    call: Call<BudgetsResponse>,
//                    response: Response<BudgetsResponse>
//                ) {
//                    Log.d("BUDGET_API", "HTTP = ${response.code()}")
//                    Log.d("BUDGET_API", "BODY = ${response.body()}")
//
//                    if (response.isSuccessful && response.body()?.success == true) {
//                        _budgets.value = response.body()!!.data
//                    } else {
//                        Log.e("BUDGET_API", "FAILED = ${response.errorBody()?.string()}")
//                        _budgets.value = emptyList()
//                    }
//                }
//
//                override fun onFailure(call: Call<BudgetsResponse>, t: Throwable) {
//                    Log.e("BUDGET_API", "NETWORK ERROR", t)
//                    _budgets.value = emptyList()
//                }
//            })
//    }
//}
