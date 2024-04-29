package com.example.mybookhub.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.Note

class NotesAdapter(
    private var note: List<Note>,
    private val onNoteClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    fun updateNoteList(newNote: List<Note>?) {
        notifyItemRangeRemoved(0, note.size)
        note = newNote ?: listOf()
        notifyItemRangeInserted(0, note.size)
    }

    override fun getItemCount() = note.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_note_item, parent, false)
        return ViewHolder(view, onNoteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(note[position])
    }

    class ViewHolder(
        itemView: View,
        onNoteClick: (Note) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private lateinit var currentNote: Note
        private val noteTitleTV = itemView.findViewById<TextView>(R.id.book_note)

        init {
            itemView.setOnClickListener{
                onNoteClick(currentNote)
            }
        }
        fun bind(note: Note) {
            currentNote = note
            noteTitleTV.text = currentNote.title
        }
    }
}