package com.example.mybookhub.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mybookhub.bean.LibraryBook
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryBookDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: LibraryBook)

    @Query("DELETE FROM library WHERE title = :title AND author = :author")
    suspend fun delete(title: String, author: String)

    @Query("DELETE FROM library")
    suspend fun deleteAll()

    @Query("SELECT * FROM library ORDER BY lastViewed DESC")
    fun getAllBooks() : Flow<List<LibraryBook>>

    @Query("SELECT * FROM library WHERE title = :title AND author = :author")
    fun getBook(title: String, author: String) : Flow<LibraryBook>

    @Query("SELECT * FROM library WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun getBookByTitleOrAuthor(query: String) : Flow<List<LibraryBook>>

    @Query("UPDATE library SET pagesRead = :pagesRead, pageCount = :pageCount WHERE title = :title AND author = :author")
    suspend fun updatePages(title: String, author: String, pagesRead: Int, pageCount: Int)

    @Query("UPDATE library SET lastViewed = :time WHERE title = :title AND author = :author")
    suspend fun updateLastViewed(time: Long, title: String, author: String)
}