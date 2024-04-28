package com.example.mybookhub.bean

import com.example.mybookhub.data.BookProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = true)
data class BooksFromProfile(
    @Json(name="reading_log_entries") val books: MutableList<BookProfile>,
    val numFound: String
)