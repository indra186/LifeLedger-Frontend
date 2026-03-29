package com.example.untitled

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.untitled.databinding.FragmentTransactionDetailBinding
import com.example.untitled.models.TransactionDetailResponse
import com.example.untitled.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private var txId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        txId = arguments?.getInt("tx_id") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (txId == 0) return

        loadTransaction(txId)
    }


    private fun loadTransaction(id: Int) {
        RetrofitClient.instance.getTransaction(id)
            .enqueue(object : Callback<TransactionDetailResponse> {
                override fun onResponse(
                    call: Call<TransactionDetailResponse>,
                    response: Response<TransactionDetailResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        bindData(response.body()!!.data)
                    }
                }

                override fun onFailure(call: Call<TransactionDetailResponse>, t: Throwable) {}
            })
    }

    private fun bindData(tx: TransactionDetailResponse.Data) {

        val sign = if (tx.type == "income") "+" else "-"
        binding.tvAmount.text = "$sign₹${tx.amount}"

        binding.tvAmount.setTextColor(
            if (tx.type == "income") Color.parseColor("#2E7D32")
            else Color.parseColor("#C62828")
        )

        // Category shown under amount
        binding.tvDescription.text = tx.category

        // Details
        binding.tvDesc.text = tx.description ?: "-"
        binding.tvAccount.text = tx.account_name ?: "-"

        // ✅ USER SELECTED DATE
        binding.tvTxDate.text = formatTxDate(tx.tx_date)

        // ✅ ACTUAL CREATED TIME
        binding.tvCreatedAt.text = formatCreatedAt(tx.created_at)
    }

    private fun formatTxDate(txDate: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return output.format(input.parse(txDate)!!)
    }

    private fun formatCreatedAt(createdAt: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return output.format(input.parse(createdAt)!!)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
