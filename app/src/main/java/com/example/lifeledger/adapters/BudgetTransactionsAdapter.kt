package com.example.lifeledger.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeledger.databinding.ItemTransactionBinding
import com.example.lifeledger.models.BudgetTransaction

class BudgetTransactionsAdapter(
    private var transactions: List<BudgetTransaction>
) : RecyclerView.Adapter<BudgetTransactionsAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(
        val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {

        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {

        val transaction = transactions[position]

        holder.binding.tvTransactionTitle.text =
            if (transaction.description.isNullOrEmpty())
                transaction.category
            else
                transaction.description

        holder.binding.tvTransactionDate.text =
            transaction.tx_date

        holder.binding.tvTransactionAmount.text =
            "-₹${transaction.amount}"
    }

    override fun getItemCount() = transactions.size

    fun updateData(newData: List<BudgetTransaction>) {
        transactions = newData
        notifyDataSetChanged()
    }
}