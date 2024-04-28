package com.example.mybookhub.data

import android.util.Log
import com.example.mybookhub.bean.BookSearch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookSearchRepository(
    private val service: OpenLibraryService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadBookSearch(query: String): Result<BookSearch> =
        withContext(ioDispatcher) {
            Log.d("BookSearchRepository", query)
            try {
                val response = service.getBooks(query)
                Log.d("get book", response.message())
                val results = response.body()
                if (response.isSuccessful && results != null) {
                    Result.success(results)
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}