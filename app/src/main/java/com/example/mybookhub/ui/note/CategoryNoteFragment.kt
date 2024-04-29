package com.example.mybookhub.ui.note

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.vm.NotesViewModel
import com.example.mybookhub.databinding.FragmentCategoryNoteBinding
import com.example.mybookhub.ui.CategoryAdapter
import com.example.mybookhub.ui.adapter.NotesAdapter
import kotlinx.coroutines.launch

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
        var selectedCategory: String? = null
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        val noteTitleET = dialogView.findViewById<EditText>(R.id.note_title)
        val newCategoryET = dialogView.findViewById<EditText>(R.id.new_category_edit_text)
        val noteContentET = dialogView.findViewById<EditText>(R.id.note_content)

        // Initialize spinner
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.category_spinner)
        val categoryAdapter = CategoryAdapter(requireContext(), android.R.layout.simple_spinner_item)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        // Set default category
        val defaultCategory = "Default"
        selectedCategory = defaultCategory
        // Observe category list
        notesViewModel.noteCategoryList.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                categoryAdapter.addAll(it.map { category -> category.title })
                categoryAdapter.notifyDataSetChanged()
            }
        }
        // Spinner item selection listener
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // if a note is clicked on, populate title and content
        if (note != null) {
            noteTitleET.setText(note.title)
            noteContentET.setText(note.content)
            categorySpinner.setSelection(categoryAdapter.getPosition(note.category))
        }

        val btnCancel = dialogView.findViewById<Button>(R.id.cancel_note)
        val btnSave = dialogView.findViewById<Button>(R.id.save_note)
        val btnDelete = dialogView.findViewById<Button>(R.id.delete_note)

        btnCancel.setOnClickListener { dialog.dismiss() }
        // If new category is entered, add it to the database

        // remove delete note button if there is no note, otherwise set click listener
        if (note == null) {
            btnDelete.visibility = View.GONE
            btnSave.setOnClickListener {
                // save the note
                val noteTitle = noteTitleET.text.toString()
                val noteContent = noteContentET.text.toString()
                if (newCategoryET.text.isNotEmpty()) {
                    val newCategory = newCategoryET.text.toString()
                    Log.d("new Category", newCategory)
                    lifecycleScope.launch {
                        notesViewModel.addCategory(NoteCategory(id = 0, newCategory, ""))
                    }
                    selectedCategory = newCategory
                }
                lifecycleScope.launch {
                    notesViewModel.addNote(
                        Note(
                            id = 0, // Set id to 0, it will be auto-generated
                            noteTitle,
                            book.title,
                            book.author,
                            selectedCategory ?: getString(R.string.note_category_placeholder),
                            noteContent
                        )
                    )
                }

                dialog.dismiss()
            }
        } else {
            btnDelete.setOnClickListener {
                showDeleteNoteConfirmationDialog(note, dialog)
            }
            btnSave.setOnClickListener {
                // save the note
                val noteTitle = noteTitleET.text.toString()
                val noteContent = noteContentET.text.toString()
                if (newCategoryET.text.isNotEmpty()) {
                    val newCategory = newCategoryET.text.toString()
                    Log.d("new Category", newCategory)
                    lifecycleScope.launch {
                        notesViewModel.addCategory(NoteCategory(id = 0, newCategory, ""))
                    }
                    selectedCategory = newCategory
                }
                notesViewModel.updateNote(
                    note,
                    noteTitle,
                    selectedCategory ?: getString(R.string.note_category_placeholder),
                    noteContent
                )
                dialog.dismiss()
            }
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