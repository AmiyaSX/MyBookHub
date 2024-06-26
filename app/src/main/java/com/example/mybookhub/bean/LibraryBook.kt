package com.example.mybookhub.bean

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "library", primaryKeys = ["title", "author"])
data class LibraryBook(
    val title: String,
    val author: String,
    val coverURL: String,
    val rating: Float? = null,
    val ratingCount: Int? = null,
    val amazonLink: String? = null,
    val pageCount: Int = 0,
    val pagesRead: Int = 0,
    val lastViewed: Long

) : Serializable