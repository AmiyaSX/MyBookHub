package com.example.mybookhub.data

import com.example.mybookhub.bean.Note
import com.example.mybookhub.data.db.NoteDao

class NotesRepository (
    private val dao: NoteDao
) {
    suspend fun insertNote(note: Note) = dao.insert(note)

    suspend fun deleteNote(note: Note) = dao.delete(note)

    fun getNotesByBook(title: String, author: String) = dao.getNotesByBook(title, author)

    fun getAllNotes() = dao.getAllNotes()

    fun searchNotes(query: String) = dao.searchNotes(query)
}