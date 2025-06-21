package com.example.libreria.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreria.data.model.Book
import com.example.libreria.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {
    
    fun getBook(isbn: String): Flow<Book?> {
        return repository.getBookByIsbn(isbn)
    }
    
    fun updateBookLocation(isbn: String, bookcaseNumber: Int, shelfNumber: Int) {
        viewModelScope.launch {
            repository.updateBookLocation(isbn, bookcaseNumber, shelfNumber)
        }
    }
    
    fun deleteBook(book: Book, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteBook(book)
            onComplete()
        }
    }
    
    fun addOrUpdateBook(book: Book, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addBook(book)
            onComplete()
        }
    }

    fun updateBookCoverPath(isbn: String, path: String?) {
        viewModelScope.launch {
            val book = repository.getBookByIsbn(isbn).first()
            if (book != null) {
                repository.addBook(book.copy(coverUrl = path))
            }
        }
    }
}
