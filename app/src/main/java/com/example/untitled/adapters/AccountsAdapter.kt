package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.models.Account

class AccountsAdapter(
    private var accounts: List<Account>
) : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_account_name)
        val tvBalance: TextView = itemView.findViewById(R.id.tv_account_balance)
        val tvType: TextView = itemView.findViewById(R.id.tv_account_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.tvName.text = account.account_name
        holder.tvBalance.text = "₹${account.balance}"
        holder.tvType.text = account.type
    }

    override fun getItemCount() = accounts.size

    fun updateAccounts(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}
