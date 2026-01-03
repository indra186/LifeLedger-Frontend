package com.example.untitled

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageResId: Int // Placeholder for now, can be updated later
)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val textDescription: TextView = itemView.findViewById(R.id.tv_description)
        private val imageOnboarding: ImageView = itemView.findViewById(R.id.iv_onboarding_image)

        fun bind(item: OnboardingItem) {
            textTitle.text = item.title
            textDescription.text = item.description
            imageOnboarding.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_onboarding_page, parent, false
        )
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
