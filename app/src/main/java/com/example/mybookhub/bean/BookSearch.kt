package com.example.mybookhub.bean

import com.example.mybookhub.data.Book
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookSearch(
    @Json(name="docs") val books: MutableList<Book>,
    val numFound: String
)