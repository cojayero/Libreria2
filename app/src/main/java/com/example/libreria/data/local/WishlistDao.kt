package com.example.libreria.data.local

import androidx.room.*
import com.example.libreria.data.model.WishlistBook
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY addedDate DESC")
    fun getAllWishlistBooks(): Flow<List<WishlistBook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistBook(book: WishlistBook)

    @Delete
    suspend fun deleteWishlistBook(book: WishlistBook)

    @Query("SELECT * FROM wishlist WHERE isbn = :isbn LIMIT 1")
    suspend fun getWishlistBookByIsbn(isbn: String): WishlistBook?
}
