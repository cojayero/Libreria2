package com.example.libreria.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreria.data.model.WishlistBook
import com.example.libreria.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<WishlistUiState>(WishlistUiState.Loading)
    val uiState: StateFlow<WishlistUiState> = _uiState
    
    init {
        loadWishlist()
    }
    
    private fun loadWishlist() {
        viewModelScope.launch {
            repository.getAllWishlistBooks()
                .catch { 
                    _uiState.value = WishlistUiState.Error(it.message ?: "Unknown error")
                }
                .collect { books ->
                    _uiState.value = WishlistUiState.Success(books)
                }
        }
    }
    
    fun removeFromWishlist(book: WishlistBook) {
        viewModelScope.launch {
            try {
                repository.removeFromWishlist(book)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

sealed class WishlistUiState {
    object Loading : WishlistUiState()
    data class Success(val books: List<WishlistBook>) : WishlistUiState()
    data class Error(val message: String) : WishlistUiState()
}
