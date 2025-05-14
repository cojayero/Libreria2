package com.example.libreria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.libreria.data.model.Book
import com.example.libreria.data.model.WishlistBook

@Database(
    entities = [Book::class, WishlistBook::class],
    version = 1,
    exportSchema = false
)
abstract class LibreriaDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun wishlistDao(): WishlistDao

    companion object {
        const val DATABASE_NAME = "libreria_db"
    }
}
