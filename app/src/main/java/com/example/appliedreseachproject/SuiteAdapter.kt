package com.example.appliedreseachproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class SuiteAdapter(
    private val suites: List<Suite>,
    private val isCompletedView: Boolean = false,
    private val onSuiteClick: (Suite) -> Unit
) : RecyclerView.Adapter<SuiteAdapter.SuiteViewHolder>() {

    class SuiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardSuite: MaterialCardView = itemView.findViewById(R.id.cardSuite)
        val tvSuiteName: TextView = itemView.findViewById(R.id.tvSuiteName)
        val tvSuiteAddress: TextView = itemView.findViewById(R.id.tvSuiteAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuiteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suite_card, parent, false)
        return SuiteViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuiteViewHolder, position: Int) {
        val suite = suites[position]
        holder.tvSuiteName.text = suite.name
        holder.tvSuiteAddress.text = suite.address

        val context = holder.itemView.context
        if (isCompletedView || suite.isCompleted) {
            holder.cardSuite.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_completed_suites))
            holder.tvSuiteName.setTextColor(ContextCompat.getColor(context, R.color.btn_completed_suites_text))
            holder.tvSuiteAddress.setTextColor(ContextCompat.getColor(context, R.color.btn_completed_suites_text))
        } else {
            holder.cardSuite.setCardBackgroundColor(ContextCompat.getColor(context, R.color.btn_assigned_suites))
            holder.tvSuiteName.setTextColor(ContextCompat.getColor(context, R.color.btn_assigned_suites_text))
            holder.tvSuiteAddress.setTextColor(ContextCompat.getColor(context, R.color.btn_assigned_suites_text))
        }

        holder.itemView.setOnClickListener {
            onSuiteClick(suite)
        }
    }

    override fun getItemCount(): Int = suites.size
}