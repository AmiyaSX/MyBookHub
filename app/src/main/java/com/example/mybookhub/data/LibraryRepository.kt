package com.example.mybookhub.data

import com.example.mybookhub.bean.LibraryBook
import com.example.mybookhub.data.db.LibraryBookDao

class LibraryRepository (
    private val dao: LibraryBookDao
) {

    suspend fun insertBook(book: LibraryBook) = dao.insert(book)

    suspend fun deleteBook(title: String, author: String) = dao.delete(title, author)

    suspend fun deleteAllBooks() = dao.deleteAll()

    fun getBook(title: String, author: String) = dao.getBook(title, author)

    fun getAllBooks() = dao.getAllBooks()
    fun getBookByTitleOrAuthor(query: String) = dao.getBookByTitleOrAuthor(query)

    suspend fun updatePages(title: String, author: String, pagesRead: Int, pageCount: Int) =
        dao.updatePages(title, author, pagesRead, pageCount)

    suspend fun updateLastViewed(time: Long, title: String, author: String) =
        dao.updateLastViewed(time, title, author)
}