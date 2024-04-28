package com.example.mybookhub.data.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybookhub.bean.BookSearch
import com.example.mybookhub.data.BookSearchRepository
import com.example.mybookhub.data.OpenLibraryService
import com.example.mybookhub.util.LoadingStatus
import kotlinx.coroutines.launch

class BookSearchViewModel : ViewModel() {
    private val repository = BookSearchRepository(OpenLibraryService.create())

    private val _searchResults = MutableLiveData<BookSearch?>(null)
    val searchResults: LiveData<BookSearch?> = _searchResults

    private val _loadingStatus = MutableLiveData<LoadingStatus>(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadSearchResults(query: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.loadBookSearch(query)
            Log.d("BookSearchViewModel", result.toString())
            _searchResults.value = result.getOrNull()
            _error.value = result.exceptionOrNull()?.message
            _loadingStatus.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}