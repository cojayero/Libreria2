package com.example.libreria.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistBook(
    @PrimaryKey
    val isbn: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val price: Double?,
    val editorial: String?, // Nuevo campo
    val pageCount: Int?,   // Nuevo campo
    val addedDate: Long = System.currentTimeMillis()
)
