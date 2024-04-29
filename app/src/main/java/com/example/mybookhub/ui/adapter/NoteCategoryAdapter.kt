
package com.example.mybookhub.ui.adapter

import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.NoteCategory

class NoteCategoryAdapter(
    private var noteCategoryList: List<NoteCategory>,
    private val onNoteCategoryClick: (NoteCategory) -> Unit,
    private val onNoteCategoryLongClick: (NoteCategory) -> Unit,
) : RecyclerView.Adapter<NoteCategoryAdapter.ViewHolder>() {

    fun updateCategoryList(newNoteCategoryList: List<NoteCategory>?) {
        notifyItemRangeRemoved(0, noteCategoryList.size)
        noteCategoryList = newNoteCategoryList ?: listOf()
        notifyItemRangeInserted(0, noteCategoryList.size)
    }

    override fun getItemCount() = noteCategoryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_category_item, parent, false)
        return ViewHolder(view, onNoteCategoryClick, onNoteCategoryLongClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noteCategoryList[position])
    }

    class ViewHolder(
        itemView: View,
        onNoteCategoryClick: (NoteCategory) -> Unit,
        onNoteCategoryLongClick: (NoteCategory) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private lateinit var currentNoteCategory: NoteCategory
        private val categoryTitleTV = itemView.findViewById<TextView>(R.id.note_category_name)
        private val categoryDesTV = itemView.findViewById<TextView>(R.id.note_category_description)

        init {
            itemView.setOnClickListener{
                onNoteCategoryClick(currentNoteCategory)
            }
            itemView.setOnLongClickListener {
                onNoteCategoryLongClick(currentNoteCategory)
                true
             }
        }
        fun bind(noteCategory: NoteCategory) {
            currentNoteCategory = noteCategory
            categoryTitleTV.text = currentNoteCategory.title
            categoryDesTV.text = currentNoteCategory.description
            Log.d("note_category_description", currentNoteCategory.description)
        }
    }
}