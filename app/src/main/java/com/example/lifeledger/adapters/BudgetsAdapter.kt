package com.example.lifeledger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeledger.R
import com.example.lifeledger.models.Budget
import com.example.lifeledger.utils.CategoryIconHelper

class BudgetsAdapter(
    private var budgets: List<Budget>,
    private val onBudgetClick: (Budget) -> Unit

) : RecyclerView.Adapter<BudgetsAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        val tvCategoryVal: TextView = itemView.findViewById(R.id.tv_category_val)
        val pbCategory: ProgressBar = itemView.findViewById(R.id.pb_category)
        val ivIcon: ImageView =
            itemView.findViewById(R.id.iv_category_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_category, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]

        holder.tvCategoryName.text = budget.category

        val spent = budget.spent_amount.toDouble()
        val limit = budget.limit_amount.toDouble()


        holder.tvCategoryVal.text = "₹$spent spent of ₹$limit"

        val progress = if (limit > 0) ((spent / limit) * 100).toInt() else 0
        holder.pbCategory.progress = progress

        holder.itemView.setOnClickListener {
            onBudgetClick(budget)
        }
        holder.ivIcon.setImageResource(
            CategoryIconHelper.getCategoryIcon(budget.category)
        )
    }

    override fun getItemCount() = budgets.size

    fun updateBudgets(newBudgets: List<Budget>) {
        budgets = newBudgets
        notifyDataSetChanged()
    }
}
