package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.models.TransactionItem
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(
    private var transactions: List<TransactionItem>,
    private val onItemClick: (TransactionItem) -> Unit
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_transaction_title)
        val tvDate: TextView = itemView.findViewById(R.id.tv_transaction_date)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_transaction_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvTitle.text = transaction.category ?: "Uncategorized"
        holder.tvDate.text = formatTxDateTime(transaction.tx_date, transaction.created_at)

        val sign = if (transaction.type == "income") "+" else "-"
        holder.tvAmount.text = "$sign₹${transaction.amount}"

        val color = if (transaction.type == "income")
            holder.itemView.context.getColor(android.R.color.holo_green_dark)
        else
            holder.itemView.context.getColor(android.R.color.holo_red_dark)

        holder.tvAmount.setTextColor(color)

        // ✅ ONLY click handler (THIS is what was missing)
        holder.itemView.setOnClickListener {
            android.util.Log.d("TX_CLICK", "Clicked tx_id=${transaction.id}")
            onItemClick(transaction)
        }

    }

    override fun getItemCount() = transactions.size

    fun updateApiTransactions(newList: List<TransactionItem>) {
        transactions = newList
        notifyDataSetChanged()
    }

    private fun formatTxDateTime(txDate: String, createdAt: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())

        val datePart = dateFormat.parse(txDate)!!
        val timePart = timeFormat.parse(createdAt)!!

        val cal = Calendar.getInstance()
        cal.time = datePart

        val timeCal = Calendar.getInstance()
        timeCal.time = timePart

        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))

        return output.format(cal.time)
    }
}
