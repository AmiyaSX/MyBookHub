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

    suspend fun updateNote(id: Long, title: String, category: String, content: String) = dao.updateNote(id, title, category, content)

    suspend fun deleteNote(note: Note) = dao.delete(note)

    suspend fun insertCategory(category: NoteCategory): Long = categoryDao.insert(category)
    suspend fun updateCategory(id: Long, tile: String, description: String) = categoryDao.updateCategory(id, tile, description)

    suspend fun deleteCategory(category: NoteCategory) = categoryDao.delete(category)

    fun getAllCategory() = categoryDao.getAllNoteCategories()

    fun getCategoryByTitle(title: String) = categoryDao.getCategoryByTitle(title)

    fun getNotesByBook(title: String, author: String) = dao.getNotesByBook(title, author)

    fun getNotesByCategory(category: String) = dao.getNotesByCategory(category)

    fun getAllNotes() = dao.getAllNotes()

    fun searchNotes(query: String) = dao.searchNotes(query)
}