package com.example.mybookhub.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R

class RecentSearchAdapter(
    private var recentSearches: List<String>,
    private val onSearchClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_search_item, parent, false)
        return RecentSearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        val recentSearch = recentSearches[position]
        holder.bind(recentSearch)
    }

    override fun getItemCount(): Int = recentSearches.size

    fun getRecentSearches(): List<String> {
        return recentSearches
    }
    fun updateRecentSearches(newRecentSearches: List<String>) {
        recentSearches = newRecentSearches.toList()
        notifyDataSetChanged()
    }

    inner class RecentSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recentSearchTextView: TextView = itemView.findViewById(R.id.text_recent_search)

        fun bind(recentSearch: String) {
            recentSearchTextView.text = recentSearch
            itemView.setOnClickListener {
                onSearchClick(recentSearch)
            }
        }
    }
}