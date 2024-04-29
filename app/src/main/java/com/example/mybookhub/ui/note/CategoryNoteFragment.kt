package com.example.mybookhub.ui.note

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.vm.NotesViewModel
import com.example.mybookhub.databinding.FragmentCategoryNoteBinding
import com.example.mybookhub.ui.adapter.NotesAdapter

class CategoryNoteFragment : Fragment() {
    private var _binding: FragmentCategoryNoteBinding? = null
    private val binding get() = _binding!!

    private val notesViewModel: NotesViewModel by viewModels()
    private val notesAdapter = NotesAdapter(emptyList(), ::showNoteDialog)
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var book: LibraryBook
    private lateinit var category: NoteCategory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // notes recycler view
        notesRecyclerView = view.findViewById(R.id.note_recycler_view)
        notesRecyclerView.adapter = notesAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(context)

        category = arguments?.getSerializable("noteCategory") as NoteCategory
        notesViewModel.getNotesByCategory(category.title).observe(viewLifecycleOwner) { notes ->
            notesAdapter.updateNoteList(notes)
        }

    }
    private fun showNoteDialog(note: Note?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val noteTitleET = dialogView.findViewById<EditText>(R.id.note_title)
        val noteContentET = dialogView.findViewById<EditText>(R.id.note_content)

        // if a note is clicked on, populate title and content
        if (note != null) {
            noteTitleET.setText(note.title)
            noteContentET.setText(note.content)
        }

        val btnCancel = dialogView.findViewById<Button>(R.id.cancel_note)
        val btnSave = dialogView.findViewById<Button>(R.id.save_note)
        val btnDelete = dialogView.findViewById<Button>(R.id.delete_note)

        btnCancel.setOnClickListener { dialog.dismiss() }

        // remove delete note button if there is no note, otherwise set click listener
        if (note == null) {
            btnDelete.visibility = View.GONE
        } else {
            btnDelete.setOnClickListener {
                showDeleteNoteConfirmationDialog(note, dialog)
            }
        }

        btnSave.setOnClickListener {
            // save the note
            val noteTitle = noteTitleET.text.toString()
            val noteContent = noteContentET.text.toString()
            notesViewModel.addNote(
                Note(
                    noteTitle,
                    book.title,
                    book.author,
                    getString(R.string.note_category_placeholder),
                    noteContent
                )
            )

            dialog.dismiss()
        }

        dialog.show()
    }
    private fun showDeleteNoteConfirmationDialog(note: Note, noteDialog: AlertDialog) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete_book, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_delete_book)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_delete_book)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        val headerText = dialogView.findViewById<TextView>(R.id.delete_book_header)
        val bodyText = dialogView.findViewById<TextView>(R.id.delete_book_message)
        headerText.text = getString(R.string.delete_note_header_text)
        bodyText.text = getString(R.string.delete_note_dialog_message)

        confirmButton.setOnClickListener {
            //delete note from db
            notesViewModel.deleteNote(note)

            noteDialog.dismiss()
            dialog.dismiss()
        }

        dialog.show()
    }

}