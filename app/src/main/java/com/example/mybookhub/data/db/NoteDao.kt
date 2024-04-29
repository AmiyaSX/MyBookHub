package com.example.mybookhub.data.db

import androidx.room.*
import com.example.mybookhub.bean.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE bookTitle = :title AND bookAuthor = :author")
    fun getNotesByBook(title: String, author: String) : Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE category = :category")
    fun getNotesByCategory(category: String) : Flow<List<Note>>

    @Query("SELECT * FROM notes")
    fun getAllNotes() : Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String) : Flow<List<Note>>
}