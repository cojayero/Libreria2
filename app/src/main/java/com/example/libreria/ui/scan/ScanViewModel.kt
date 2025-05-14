package com.example.libreria.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreria.data.model.Book
import com.example.libreria.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Initial)
    val uiState: StateFlow<ScanUiState> = _uiState

    fun onBarcodeDetected(isbn: String) {
        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading
            try {
                val result = repository.searchBookByIsbn(isbn)
                result.fold(
                    onSuccess = { book ->
                        _uiState.value = ScanUiState.BookFound(book)
                    },
                    onFailure = { exception ->
                        _uiState.value = ScanUiState.Error(exception.message ?: "Error finding book")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addBookToLibrary(book: Book) {
        viewModelScope.launch {
            try {
                repository.addBook(book)
                _uiState.value = ScanUiState.BookSaved
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Error saving book")
            }
        }
    }

    fun resetState() {
        _uiState.value = ScanUiState.Initial
    }
}

sealed class ScanUiState {
    object Initial : ScanUiState()
    object Loading : ScanUiState()
    data class BookFound(val book: Book) : ScanUiState()
    object BookSaved : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}
