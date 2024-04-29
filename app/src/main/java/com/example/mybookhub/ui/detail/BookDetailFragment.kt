package com.example.mybookhub.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.*
import com.example.mybookhub.data.db.AppDatabase
import com.example.mybookhub.databinding.FragmentBookDetailBinding
import com.example.mybookhub.ui.adapter.BookDetailAdapter
import com.example.mybookhub.data.vm.BookDetailViewModel
import com.example.mybookhub.data.vm.NotesViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookDetailViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val libraryRepository = LibraryRepository(database.libraryBookDao())
        val notesRepository = NotesRepository(database.noteDao(), database.noteCategoryDao())
        BookDetailViewModelFactory(libraryRepository, notesRepository)
    }

    private val notesViewModel: NotesViewModel by viewModels()
    private val notesAdapter = BookDetailAdapter(emptyList(), ::showNoteDialog)
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var book: LibraryBook

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // notes recycler view
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView)
        notesRecyclerView.adapter = notesAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Retrieve the LibraryBook object from the fragment's arguments.
        book = arguments?.getSerializable("bookDetails") as LibraryBook

        // update last viewed time for this book
        viewModel.updateLastViewed(System.currentTimeMillis(), book.title, book.author)

        book.let {
            // Update UI elements with the book details.
            binding.bookTitleText.text = it.title
            binding.bookAuthorText.text = it.author
            binding.pagesReadButton.text = getString(R.string.pages_read_button_text, it.pagesRead, it.pageCount)
            binding.bookRatingBar.rating = it.rating ?: 0.0f

            Glide.with(this)
                .load(it.coverURL)
                .placeholder(R.drawable.baseline_book_24)
                .error(R.drawable.baseline_book_24)
                .into(binding.bookCoverImage)

            // Handle 'Add Note' button click
            binding.addNoteButton.setOnClickListener {
                showNoteDialog(null)
            }
        }

        // pages read button listener
        binding.pagesReadButton.setOnClickListener {
            showPagesDialog()
        }

        binding.deleteBookButton.setOnClickListener {
            showDeleteBookConfirmationDialog()
        }

        // observe notes viewModel
        notesViewModel.getNotesByBook(book.title, book.author)
            .observe(viewLifecycleOwner) { notes ->
                notesAdapter.updateNotesList(notes)
            }

        // observe changes in book (such as pages read/count)
        viewModel.getBookDetails(book.title, book.author).observe(viewLifecycleOwner) { updatedBook ->
            book = updatedBook
        }

        val searchView = view.findViewById<SearchView>(R.id.notes_search_view)


        // set up search bar/change listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { queryString ->
                    notesViewModel.searchNotes(queryString).observe(viewLifecycleOwner) {notes ->
                        notesAdapter.updateNotesList(notes)
                    }
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showNoteDialog(note: Note?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        var selectedCategory: String? = null
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        val categoryList = notesViewModel.noteCategoryList.value
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
        categoryAdapter.add(defaultCategory)
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
        if (newCategoryET.text.isNotEmpty()) {
            val newCategory = newCategoryET.text.toString()
            lifecycleScope.launch {
                notesViewModel.addCategory(NoteCategory(id = 0, newCategory, ""))
            }
            selectedCategory = newCategory
        }
        // remove delete note button if there is no note, otherwise set click listener
        if (note == null) {
            btnDelete.visibility = View.GONE
            btnSave.setOnClickListener {
                // save the note
                val noteTitle = noteTitleET.text.toString()
                val noteContent = noteContentET.text.toString()
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

    private fun showPagesDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_pages, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // update ets
        val totalPagesET = dialogView.findViewById<EditText>(R.id.total_pages_et)
        totalPagesET.hint = book.pageCount.toString()
        val pagesReadET = dialogView.findViewById<EditText>(R.id.pages_read_et)
        pagesReadET.hint = book.pagesRead.toString()

        // set button click listeners
        val btnCancel = dialogView.findViewById<Button>(R.id.cancel_pages)
        val btnSave = dialogView.findViewById<Button>(R.id.save_pages)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            // save the pages
            var totalPages = totalPagesET.text.toString()
            var pagesRead = pagesReadET.text.toString()

            // only update the button and db if there were changes
            if (pagesRead.toIntOrNull() != book.pagesRead ||
                totalPages.toIntOrNull() != book.pageCount) {

                if (totalPages.isEmpty())
                    totalPages = book.pageCount.toString()

                if (pagesRead.isEmpty())
                    pagesRead = book.pagesRead.toString()

                // set text for pages read button
                binding.pagesReadButton.text = getString(
                    R.string.pages_read_button_text,
                    pagesRead.toInt(),
                    totalPages.toInt()
                )

                // update db
                viewModel.updatePages(
                    book.title,
                    book.author,
                    pagesRead.toInt(),
                    totalPages.toInt()
                )
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeleteBookConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete_book, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_delete_book)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_delete_book)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            // remove observer to prevent app crash
            viewModel.getBookDetails(book.title, book.author).removeObservers(viewLifecycleOwner)

            //delete book from db (notes cascade delete automatically)
            viewModel.removeBook(
                book.title,
                book.author
            )

            // navigate back to the library (clear backwards nav first so you can't go back
            // to the book details page of the deleted book)
            findNavController().popBackStack(R.id.library, false)

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

class CategoryAdapter(context: Context, resource: Int) : ArrayAdapter<String>(context, resource) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getItem(position)
        return view
    }
}
class BookDetailViewModelFactory(
    private val libraryRepository: LibraryRepository,
    private val notesRepository: NotesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookDetailViewModel(libraryRepository, notesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

