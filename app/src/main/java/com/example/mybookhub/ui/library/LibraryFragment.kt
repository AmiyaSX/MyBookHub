package com.example.mybookhub.ui.library

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.ui.vm.LibraryViewModel
import com.example.mybookhub.ui.vm.NotesViewModel
import com.example.mybookhub.ui.adapter.LibraryAdapter


class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val tag: String = "LibraryFragment"
    private val viewModel: LibraryViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()

    private lateinit var books: List<LibraryBook>
    private var notes: MutableList<Note> = mutableListOf()

    private val libraryAdapter = LibraryAdapter(emptyList(), ::onLibraryBookClick)
    private  lateinit var libraryRecyclerView: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryRecyclerView = view.findViewById(R.id.library_recycler_view)
        libraryRecyclerView.adapter = libraryAdapter
        libraryRecyclerView.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

        viewModel.libraryBooks.observe(viewLifecycleOwner) {books ->
            libraryAdapter.updateLibraryList(books)
        }

        val searchView = view.findViewById<SearchView>(R.id.library_search_view)

        // set up search bar/change listener to filter library by query
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { queryString ->
                    viewModel.getBookByTitleOrAuthor(queryString).observe(viewLifecycleOwner) { books ->
                        libraryAdapter.updateLibraryList(books)
                    }
                }
                return true
            }
        })
    }

    private fun onLibraryBookClick(book: LibraryBook) {
        val action = LibraryFragmentDirections.navigateToBookDetails(book)
        findNavController().navigate(action)
    }
}