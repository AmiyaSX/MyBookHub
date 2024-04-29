package com.example.mybookhub.data.db

import androidx.room.*
import com.example.mybookhub.bean.NoteCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteCategory: NoteCategory): Long

    @Update
    suspend fun update(noteCategory: NoteCategory)

    @Query("UPDATE category SET title = :title, description = :description WHERE id = :id")
    suspend fun updateCategory(id: Long, title: String, description: String)

    @Delete
    suspend fun delete(noteCategory: NoteCategory)

    @Query("SELECT * FROM category")
    fun getAllNoteCategories() : Flow<List<NoteCategory>>

    @Query("SELECT * FROM category where title = :title")
    fun getCategoryByTitle(title: String) : Flow<NoteCategory>


}