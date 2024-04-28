package com.example.mybookhub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mybookhub.data.LibraryRepository
import com.example.mybookhub.data.Note
import com.example.mybookhub.data.NotesRepository
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val libraryRepository: LibraryRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    fun getBookDetails(title: String, author: String) =
        libraryRepository.getBook(title, author).asLiveData()

    fun getNotesByBook(title: String, author: String) =
        notesRepository.getNotesByBook(title, author).asLiveData()

    fun insertNote(note: Note) = viewModelScope.launch {
        notesRepository.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.deleteNote(note)
    }

    fun updateLastViewed(time: Long, title: String, author: String) {
        viewModelScope.launch {
            libraryRepository.updateLastViewed(time, title, author)
        }
    }

    fun updatePages(title: String, author: String, pagesRead: Int, pageCount: Int) {
        viewModelScope.launch {
            libraryRepository.updatePages(title, author, pagesRead, pageCount)
        }
    }

    fun removeBook(title: String, author: String) {
        viewModelScope.launch {
            libraryRepository.deleteBook(title, author)
        }
    }
}
