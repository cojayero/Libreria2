package com.example.libreria.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBookByIsbn(@Query("q") isbn: String): GoogleBooksResponse
}

data class GoogleBooksResponse(
    val items: List<VolumeInfo>?
)

data class VolumeInfo(
    val volumeInfo: BookInfo,
    val saleInfo: SaleInfo?
)

data class BookInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?,
    val averageRating: Double?
)

data class SaleInfo(
    val listPrice: Price?
)

data class Price(
    val amount: Double
)

data class ImageLinks(
    val thumbnail: String?
)
