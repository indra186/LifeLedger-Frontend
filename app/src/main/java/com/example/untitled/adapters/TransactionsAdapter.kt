package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.data.local.entities.TransactionEntity

class TransactionsAdapter(
    private var transactions: List<TransactionEntity>
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_transaction_title)
        val tvDate: TextView = itemView.findViewById(R.id.tv_transaction_date)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_transaction_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvTitle.text = transaction.title
        holder.tvDate.text = transaction.date
        
        val sign = if (transaction.type == "income") "+" else "-"
        holder.tvAmount.text = "$sign$${transaction.amount}"
        
        // Optional: color coding
        val color = if (transaction.type == "income") 
            holder.itemView.context.getColor(android.R.color.holo_green_dark)
        else 
            holder.itemView.context.getColor(android.R.color.holo_red_dark)
            
        holder.tvAmount.setTextColor(color)
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
