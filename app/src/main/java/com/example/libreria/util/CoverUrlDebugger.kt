package com.example.libreria.util

import android.util.Log
import com.example.libreria.data.remote.GoogleBooksApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CoverUrlDebugger {
    fun debugCoverUrl(api: GoogleBooksApi, isbn: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.searchBookByIsbn("isbn:$isbn")
                val volume = response.items?.firstOrNull()
                val volumeInfo = volume?.volumeInfo
                val rawCoverUrl = volumeInfo?.imageLinks?.thumbnail
                Log.d("CoverUrlDebugger", "rawCoverUrl: $rawCoverUrl")
            } catch (e: Exception) {
                Log.e("CoverUrlDebugger", "Error: ${e.message}")
            }
        }
    }
}
