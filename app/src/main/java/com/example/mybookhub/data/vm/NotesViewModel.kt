package com.example.mybookhub.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mybookhub.data.db.AppDatabase
import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.NotesRepository
import kotlinx.coroutines.launch
class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(
        AppDatabase.getInstance(application).noteDao(),
        AppDatabase.getInstance(application).noteCategoryDao()
    )

    val noteCategoryList = repository.getAllCategory().asLiveData()
    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun searchNotes(query: String) =
        repository.searchNotes(query).asLiveData()

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun addCategory(category: NoteCategory) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }
    fun deleteCategory(category: NoteCategory) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun getNotesByBook(title: String, author: String) =
        repository.getNotesByBook(title, author).asLiveData()

    fun getAllNotes() = repository.getAllNotes().asLiveData()

    fun getAllCategory() = repository.getAllCategory().asLiveData()

    fun getNotesByCategory(category: String) = repository.getNotesByCategory(category).asLiveData()

}