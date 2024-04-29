package com.example.mybookhub.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mybookhub.data.db.AppDatabase
import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.NotesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(
        AppDatabase.getInstance(application).noteDao(),
        AppDatabase.getInstance(application).noteCategoryDao()
    )

    val noteCategoryList = repository.getAllCategory().asLiveData()
    suspend fun addNote(note: Note): Long {
        return viewModelScope.async {
            repository.insertNote(note)
        }.await()
    }
    fun updateNote(note: Note, updateTitle: String, updateCategory: String, updateContent: String) {
        viewModelScope.launch {
            repository.updateNote(note.id, updateTitle, updateCategory, updateContent)
        }
    }
    fun updateNoteCategory(note: Note, updateCategory: String) {
        viewModelScope.launch {
            repository.updateNote(note.id, note.title, updateCategory, note.content)
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

    suspend fun addCategory(category: NoteCategory): Long {
        return viewModelScope.async {
            repository.insertCategory(category)
        }.await()
    }
     fun updateCategory(category: NoteCategory, updateTitle: String, updateDescription: String) {
         viewModelScope.launch {
            repository.updateCategory(category.id, updateTitle, updateDescription)
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