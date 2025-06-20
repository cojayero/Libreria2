package com.example.libreria.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.libreria.R
import com.example.libreria.ui.library.LibraryScreen
import com.example.libreria.ui.scan.ScanScreen
import com.example.libreria.ui.wishlist.WishlistScreen
import com.example.libreria.ui.wishlist.WishlistDetailScreen
import com.example.libreria.ui.BookDetailScreen
import com.example.libreria.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibreriaApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibreriaApp() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Screen.Library.route, Screen.Scan.route, Screen.Wishlist.route)) {
                LibreriaBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Library.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Library.route) {
                LibraryScreen(navController = navController)
            }
            composable(Screen.Scan.route) {
                ScanScreen(navController = navController)
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(navController = navController)
            }
            composable(Screen.BookDetail.route) { backStackEntry ->
                val isbn = backStackEntry.arguments?.getString("isbn")
                isbn?.let {
                    BookDetailScreen(
                        isbn = it,
                        navController = navController
                    )
                }
            }
            composable(Screen.WishlistDetail.route) { backStackEntry ->
                val isbn = backStackEntry.arguments?.getString("isbn")
                isbn?.let {
                    WishlistDetailScreen(
                        isbn = it,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun LibreriaBottomBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Book, contentDescription = null) },
            label = { Text("Library") },
            selected = currentRoute == Screen.Library.route,
            onClick = {
                if (currentRoute != Screen.Library.route) {
                    navController.navigate(Screen.Library.route) {
                        popUpTo(Screen.Library.route) { inclusive = true }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Camera, contentDescription = null) },
            label = { Text("Scan") },
            selected = currentRoute == Screen.Scan.route,
            onClick = {
                if (currentRoute != Screen.Scan.route) {
                    navController.navigate(Screen.Scan.route) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = null) },
            label = { Text("Wishlist") },
            selected = currentRoute == Screen.Wishlist.route,
            onClick = {
                if (currentRoute != Screen.Wishlist.route) {
                    navController.navigate(Screen.Wishlist.route) {
                        popUpTo(Screen.Wishlist.route) { inclusive = true }
                    }
                }
            }
        )
    }
}
