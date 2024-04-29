package com.example.mybookhub.data.db

import androidx.room.*
import com.example.mybookhub.bean.NoteCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteCategory: NoteCategory)

    @Update
    suspend fun update(noteCategory: NoteCategory)

    @Delete
    suspend fun delete(noteCategory: NoteCategory)

    @Query("SELECT * FROM category")
    fun getAllNoteCategories() : Flow<List<NoteCategory>>

}