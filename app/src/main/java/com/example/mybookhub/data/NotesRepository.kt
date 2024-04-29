package com.example.mybookhub.data

import com.example.mybookhub.bean.Note
import com.example.mybookhub.bean.NoteCategory
import com.example.mybookhub.data.db.NoteCategoryDao
import com.example.mybookhub.data.db.NoteDao

class NotesRepository (
    private val dao: NoteDao,
    private val categoryDao: NoteCategoryDao
) {
    suspend fun insertNote(note: Note) = dao.insert(note)
    suspend fun updateNote(note: Note) = dao.update(note)

    suspend fun deleteNote(note: Note) = dao.delete(note)

    suspend fun insertCategory(category: NoteCategory) = categoryDao.insert(category)
    suspend fun updateCategory(category: NoteCategory) = categoryDao.update(category)

    suspend fun deleteCategory(category: NoteCategory) = categoryDao.delete(category)

    fun getAllCategory() = categoryDao.getAllNoteCategories()
    fun getCategoryByTitle(title: String) = categoryDao.getCategoryByTitle(title)

    fun getNotesByBook(title: String, author: String) = dao.getNotesByBook(title, author)

    fun getNotesByCategory(category: String) = dao.getNotesByCategory(category)

    fun getAllNotes() = dao.getAllNotes()

    fun searchNotes(query: String) = dao.searchNotes(query)
}