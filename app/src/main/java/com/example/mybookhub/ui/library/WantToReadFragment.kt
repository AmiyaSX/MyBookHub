package com.example.mybookhub.ui.library

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybookhub.R
import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.bean.Note
import com.example.mybookhub.ui.adapter.LibraryAdapter
import com.example.mybookhub.data.vm.LibraryViewModel
import com.example.mybookhub.data.vm.NotesViewModel
import com.example.mybookhub.data.vm.OnlineLibraryViewModel
import com.example.mybookhub.ui.adapter.OnlineLibraryAdapter

class WantToReadFragment : Fragment(R.layout.fragment_want_to_read) {

    private val viewModel: OnlineLibraryViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()
    private val onlineLibraryAdapter = OnlineLibraryAdapter(emptyList())
    private lateinit var libraryRecyclerView: RecyclerView

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("MyBookHubPrefs", Context.MODE_PRIVATE)
    }
    // SharedPreferences key for account name
    private val ACCOUNT_NAME_KEY = "account_name"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val account = sharedPreferences.getString(ACCOUNT_NAME_KEY, "")

        libraryRecyclerView = view.findViewById(R.id.library_recycler_view)
        libraryRecyclerView.adapter = onlineLibraryAdapter
        libraryRecyclerView.layoutManager = LinearLayoutManager(context)

        if (account != null) {
            viewModel.loadWantToReadBooks(account)
        }

        viewModel.wantToReadBooksResults.observe(viewLifecycleOwner) {books ->
            onlineLibraryAdapter.updateBookList(books)
        }

        val searchView = view.findViewById<SearchView>(R.id.library_search_view)

        // set up search bar/change listener to filter library by query
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { queryString ->

                }
                return true
            }
        })
    }
}