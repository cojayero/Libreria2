package com.example.libreria.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreria.data.model.Book
import com.example.libreria.data.model.WishlistBook
import com.example.libreria.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistDetailViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {
    
    fun getWishlistBook(isbn: String): Flow<WishlistBook?> {
        return repository.getAllWishlistBooks().map { books ->
            books.find { it.isbn == isbn }
        }
    }
    
    fun moveToLibrary(wishlistBook: WishlistBook, bookcaseNumber: Int, shelfNumber: Int) {
        viewModelScope.launch {
            // Create a Book from WishlistBook
            val book = Book(
                isbn = wishlistBook.isbn,
                title = wishlistBook.title,
                author = wishlistBook.author,
                coverUrl = wishlistBook.coverUrl,
                price = wishlistBook.price,
                review = null,
                synopsis = null,
                bookcaseNumber = bookcaseNumber,
                shelfNumber = shelfNumber,
                editorial = null, // No disponible desde WishlistBook
                pageCount = null  // No disponible desde WishlistBook
            )
            
            // Add to library and remove from wishlist
            repository.addBook(book)
            repository.removeFromWishlist(wishlistBook)
        }
    }
}
