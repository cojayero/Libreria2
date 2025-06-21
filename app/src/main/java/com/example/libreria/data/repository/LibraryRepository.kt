package com.example.libreria.data.repository

import com.example.libreria.data.local.BookDao
import com.example.libreria.data.local.WishlistDao
import com.example.libreria.data.model.Book
import com.example.libreria.data.model.WishlistBook
import com.example.libreria.data.remote.GoogleBooksApi
import com.example.libreria.util.CoverUrlDebugger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val bookDao: BookDao,
    private val wishlistDao: WishlistDao,
    private val booksApi: GoogleBooksApi
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()
    
    fun getAllWishlistBooks(): Flow<List<WishlistBook>> = wishlistDao.getAllWishlistBooks()
    
    fun getBookByIsbn(isbn: String): Flow<Book?> = bookDao.getBookByIsbn(isbn)
    
    suspend fun searchBookByIsbn(isbn: String): Result<Book> {
        return try {
            val response = booksApi.searchBookByIsbn("isbn:$isbn")
            // Debug: log the cover URL
            CoverUrlDebugger.debugCoverUrl(booksApi, isbn)
            val volume = response.items?.firstOrNull()
            val volumeInfo = volume?.volumeInfo ?: return Result.failure(Exception("Book not found"))
            val saleInfo = volume.saleInfo
            val rawCoverUrl = volumeInfo.imageLinks?.thumbnail
            val coverUrl = rawCoverUrl?.replace("http://", "https://")
            val book = Book(
                isbn = isbn,
                title = volumeInfo.title,
                author = volumeInfo.authors?.joinToString(", ") ?: "Unknown",
                coverUrl = coverUrl,
                price = saleInfo?.listPrice?.amount,
                review = null,
                synopsis = volumeInfo.description,
                bookcaseNumber = null,
                shelfNumber = null,
                editorial = volumeInfo.publisher,
                pageCount = volumeInfo.pageCount
            )
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addBook(book: Book) {
        bookDao.insertBook(book)
    }
    
    suspend fun addToWishlist(book: WishlistBook) {
        wishlistDao.insertWishlistBook(book)
    }
    
    suspend fun removeFromWishlist(book: WishlistBook) {
        wishlistDao.deleteWishlistBook(book)
    }
    
    suspend fun updateBookLocation(isbn: String, bookcaseNumber: Int, shelfNumber: Int) {
        bookDao.updateBookLocation(isbn, bookcaseNumber, shelfNumber)
    }
    
    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }
}
