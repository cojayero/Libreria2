package com.example.libreria.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val isbn: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val price: Double?,
    val review: String?,
    val synopsis: String?,
    val bookcaseNumber: Int?,
    val shelfNumber: Int?,
    val addedDate: Long = System.currentTimeMillis()
)
