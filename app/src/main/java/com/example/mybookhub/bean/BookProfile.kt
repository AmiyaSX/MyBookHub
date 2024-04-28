package com.example.mybookhub.data

import android.util.Log
import java.io.Serializable
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class Work(
    val title: String,
    val author: String? = null,
    val coverURL: String? = null,
)

@JsonClass(generateAdapter = true)
data class BookProfile(
    @Json(name="work") val work: Work,
    val logged_date: String? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class WorkJson(
    val title: String,
    val author_names: List<String>? = null,
    val cover_id: String? = null
)

class OpenLibraryWorkJsonAdapter {
    @FromJson
    fun workFromJson(work: WorkJson): Work {
        Log.d("Book", "book: $work")
        return Work(
            title = work.title,
            author = work.author_names?.let{work.author_names[0]},
            coverURL = work.cover_id?.let{"https://covers.openlibrary.org/b/id/${work.cover_id}-M.jpg"}
        )
    }

    @ToJson
    fun workToJson(work: Work): String {
        throw UnsupportedOperationException("encoding Book to JSON is not supported")
    }
}