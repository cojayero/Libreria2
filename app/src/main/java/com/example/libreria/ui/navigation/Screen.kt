package com.example.libreria.ui.navigation

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Scan : Screen("scan")
    object Wishlist : Screen("wishlist")
    object BookDetail : Screen("book_detail/{isbn}") {
        fun createRoute(isbn: String) = "book_detail/$isbn"
    }
    object WishlistDetail : Screen("wishlist_detail/{isbn}") {
        fun createRoute(isbn: String) = "wishlist_detail/$isbn"
    }
    object EditBook : Screen("editBook/{isbn}") {
        fun createRoute(isbn: String) = "editBook/$isbn"
    }
}
