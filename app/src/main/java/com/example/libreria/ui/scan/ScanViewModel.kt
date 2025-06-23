package com.example.libreria.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreria.data.model.Book
import com.example.libreria.data.repository.LibraryRepository
import com.example.libreria.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
                // Verificar si el libro ya está en la biblioteca
                val bookInLibrary = repository.getBookByIsbn(isbn).first()
                if (bookInLibrary != null) {
                    _uiState.value = ScanUiState.Error("El libro ya existe en la biblioteca.")
                    return@launch
                }
                // Verificar si está en la wishlist
                val wishlistBook = repository.getWishlistBookByIsbn(isbn)
                if (wishlistBook != null) {
                    _uiState.value = ScanUiState.WishlistBookFound(wishlistBook)
                    return@launch
                }
                // Buscar en API
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

    fun addBookToLibrary(book: Book, context: android.content.Context) {
        viewModelScope.launch {
            try {
                val bookcase = book.bookcaseNumber ?: AppPreferences.getDefaultBookcase(context)?.toIntOrNull()
                val shelf = book.shelfNumber ?: AppPreferences.getDefaultShelf(context)?.toIntOrNull()
                val bookWithLocation = book.copy(
                    bookcaseNumber = bookcase,
                    shelfNumber = shelf
                )
                repository.addBook(bookWithLocation)
                _uiState.value = ScanUiState.BookSaved
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Error saving book")
            }
        }
    }

    fun moveWishlistBookToLibrary(wishlistBook: com.example.libreria.data.model.WishlistBook, context: android.content.Context) {
        viewModelScope.launch {
            try {
                // Convertir WishlistBook a Book
                val book = com.example.libreria.data.model.Book(
                    isbn = wishlistBook.isbn,
                    title = wishlistBook.title,
                    author = wishlistBook.author,
                    coverUrl = wishlistBook.coverUrl,
                    price = wishlistBook.price,
                    review = null,
                    synopsis = null,
                    bookcaseNumber = AppPreferences.getDefaultBookcase(context)?.toIntOrNull(),
                    shelfNumber = AppPreferences.getDefaultShelf(context)?.toIntOrNull(),
                    editorial = wishlistBook.editorial,
                    pageCount = wishlistBook.pageCount
                )
                repository.addBook(book)
                repository.removeFromWishlist(wishlistBook)
                _uiState.value = ScanUiState.BookSaved
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Error moviendo libro de wishlist")
            }
        }
    }

    fun addBookToWishlist(book: Book) {
        viewModelScope.launch {
            try {
                val wishlistBook = com.example.libreria.data.model.WishlistBook(
                    isbn = book.isbn,
                    title = book.title,
                    author = book.author,
                    coverUrl = book.coverUrl,
                    price = book.price,
                    editorial = book.editorial,
                    pageCount = book.pageCount
                )
                repository.addToWishlist(wishlistBook)
                _uiState.value = ScanUiState.BookSaved
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "Error añadiendo a wishlist")
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
    data class WishlistBookFound(val wishlistBook: com.example.libreria.data.model.WishlistBook) : ScanUiState()
    object BookSaved : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}
