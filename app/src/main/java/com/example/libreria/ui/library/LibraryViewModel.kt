package com.example.libreria.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreria.data.model.Book
import com.example.libreria.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState
    
    init {
        loadBooks()
    }
    
    private fun loadBooks() {
        viewModelScope.launch {
            repository.getAllBooks()
                .catch { 
                    _uiState.value = LibraryUiState.Error(it.message ?: "Unknown error")
                }
                .collect { books ->
                    _uiState.value = LibraryUiState.Success(books)
                }
        }
    }
    
    fun updateBookLocation(isbn: String, bookcaseNumber: Int, shelfNumber: Int) {
        viewModelScope.launch {
            try {
                repository.updateBookLocation(isbn, bookcaseNumber, shelfNumber)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                repository.deleteBook(book)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

sealed class LibraryUiState {
    object Loading : LibraryUiState()
    data class Success(val books: List<Book>) : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
}
