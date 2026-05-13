package com.example.untitled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.untitled.adapters.BudgetTransactionsAdapter
import com.example.untitled.databinding.FragmentBudgetDetailBinding
import com.example.untitled.models.BudgetTransactionResponse
import com.example.untitled.network.RetrofitClient
import androidx.navigation.fragment.findNavController
import com.example.untitled.utils.CategoryIconHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetDetailFragment : Fragment() {

    private var _binding: FragmentBudgetDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BudgetTransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBudgetDetailBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        val category =
            arguments?.getString("category") ?: ""

        val limit =
            arguments?.getString("limit_amount") ?: "0"

        val spent =
            arguments?.getString("spent_amount") ?: "0"

        val month =
            arguments?.getInt("month") ?: 0

        val year =
            arguments?.getInt("year") ?: 0

        binding.tvBudgetName.text = category

        binding.tvLimit.text =
            "Budget Limit ₹$limit"

        val remaining =
            limit.toDouble() - spent.toDouble()

        binding.tvBudgetStatus.text =
            "₹$remaining left"

        val limitAmount = limit.toDouble()
        val spentAmount = spent.toDouble()

        val progress =
            if (limitAmount > 0)
                ((spentAmount / limitAmount) * 100)
                    .toInt()
                    .coerceAtMost(100)
            else
                0
        binding.circularProgress.progress = progress
        binding.tvProgressPercent.text = "$progress%"
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter = BudgetTransactionsAdapter(emptyList())

        binding.rvTransactions.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvTransactions.adapter = adapter

        binding.ivBudgetIcon.setImageResource(
            CategoryIconHelper.getCategoryIcon(category)
        )
        RetrofitClient.instance.getBudgetTransactions(
            category,
            month,
            year
        ).enqueue(object : Callback<BudgetTransactionResponse> {

            override fun onResponse(
                call: Call<BudgetTransactionResponse>,
                response: Response<BudgetTransactionResponse>
            ) {

                val transactions =
                    response.body()?.data ?: emptyList()

                adapter.updateData(transactions)
            }

            override fun onFailure(
                call: Call<BudgetTransactionResponse>,
                t: Throwable
            ) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}