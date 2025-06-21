package com.example.libreria.data.local

import androidx.room.*
import com.example.libreria.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY addedDate DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    fun getBookByIsbn(isbn: String): Flow<Book?>

    @Query("SELECT * FROM books WHERE bookcaseNumber = :bookcaseNumber AND shelfNumber = :shelfNumber")
    fun getBooksInLocation(bookcaseNumber: Int, shelfNumber: Int): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("UPDATE books SET bookcaseNumber = :bookcaseNumber, shelfNumber = :shelfNumber WHERE isbn = :isbn")
    suspend fun updateBookLocation(isbn: String, bookcaseNumber: Int, shelfNumber: Int)
}
