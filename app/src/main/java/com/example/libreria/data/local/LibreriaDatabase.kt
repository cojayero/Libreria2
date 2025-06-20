package com.example.libreria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.libreria.data.model.Book
import com.example.libreria.data.model.WishlistBook

@Database(
    entities = [Book::class, WishlistBook::class],
    version = 2, // Actualizado para migración
    exportSchema = false
)
abstract class LibreriaDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun wishlistDao(): WishlistDao

    companion object {
        const val DATABASE_NAME = "libreria_db"
        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE books ADD COLUMN editorial TEXT")
                database.execSQL("ALTER TABLE books ADD COLUMN pageCount INTEGER")
                database.execSQL("ALTER TABLE wishlist ADD COLUMN editorial TEXT")
                database.execSQL("ALTER TABLE wishlist ADD COLUMN pageCount INTEGER")
            }
        }
    }
}
