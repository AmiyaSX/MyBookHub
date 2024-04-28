package com.example.mybookhub.data

import android.util.Log
import com.example.mybookhub.bean.BooksFromProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnlineLibraryRepository (
    private val service: OpenLibraryService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadCurrentReadingBooks(userId: String): Result<BooksFromProfile> =
        withContext(ioDispatcher) {
            try {
                val response = service.getCurrentReadingBooks(userId)
                Log.d("loadCurrentReadingBooks", response.message())
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
    suspend fun loadWantToReadBooks(userId: String): Result<BooksFromProfile> =
        withContext(ioDispatcher) {
            try {
                val response = service.getWantToReadBooks(userId)
                Log.d("loadWantToReadBooks", response.message())
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