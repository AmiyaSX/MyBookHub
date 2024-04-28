package com.example.mybookhub.data.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.mybookhub.bean.BooksFromProfile
import com.example.mybookhub.data.BookProfile
import com.example.mybookhub.data.OnlineLibraryRepository
import com.example.mybookhub.data.OpenLibraryService
import com.example.mybookhub.util.LoadingStatus
import kotlinx.coroutines.launch

class OnlineLibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OnlineLibraryRepository(
        OpenLibraryService.create()
    )

    private val _currentBooksResults = MutableLiveData<BooksFromProfile?>(null)
    private val _wantReadBooksResults = MutableLiveData<BooksFromProfile?>(null)

    val currentBooksResults: LiveData<BooksFromProfile?> = _currentBooksResults
    val wantToReadBooksResults: LiveData<BooksFromProfile?> = _wantReadBooksResults

    private val _loadingStatus = MutableLiveData<LoadingStatus>(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadCurrentBooks(user: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.loadCurrentReadingBooks(user)
            Log.d("BookSearchViewModel", result.toString())
            _currentBooksResults.value = result.getOrNull()
            _error.value = result.exceptionOrNull()?.message
            _loadingStatus.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }

    fun loadWantToReadBooks(user: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.loadWantToReadBooks(user)
            Log.d("BookSearchViewModel", result.toString())
            _wantReadBooksResults.value = result.getOrNull()
            _error.value = result.exceptionOrNull()?.message
            _loadingStatus.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }

}