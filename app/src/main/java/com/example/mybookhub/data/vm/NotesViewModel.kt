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
    fun updateNote(note: Note, updateTitle: String, updateContent: String) {
        val newNote = note.copy(title = updateTitle, content = updateContent)
        viewModelScope.launch {
            repository.updateNote(newNote)
        }
    }
    fun updateNoteCategory(note: Note, updateCategory: String) {
        val newNote = note.copy(category = updateCategory)
        viewModelScope.launch {
            repository.updateNote(newNote)
        }
    }
    fun updateNotesCategory(category: String, updateCategory: String) {
        val notes = getNotesByCategory(category).value
        if (notes != null) {
            for (note in notes) {
                updateNoteCategory(note, updateCategory)
            }
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
     fun updateCategory(category: NoteCategory, updateTitle: String, updateDescription: String) {
         val newCategory = category.copy(title = updateTitle, description = updateDescription)
         viewModelScope.launch {
            repository.updateCategory(newCategory)
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

    fun getCategoryByTitle(title: String) = repository.getCategoryByTitle(title).asLiveData()

    fun getNotesByCategory(category: String) = repository.getNotesByCategory(category).asLiveData()

}