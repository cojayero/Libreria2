package com.example.libreria.ui.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.libreria.data.model.Book
import com.example.libreria.data.model.WishlistBook
import com.example.libreria.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailScreen(
    isbn: String,
    navController: NavController,
    viewModel: WishlistDetailViewModel = hiltViewModel()
) {
    val book by viewModel.getWishlistBook(isbn).collectAsState(initial = null)
    var showMoveToLibraryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlist Book Details") },
                navigationIcon = {                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMoveToLibraryDialog = true }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Move to library")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            book?.let { bookData ->
                WishlistDetailContent(book = bookData)
                  if (showMoveToLibraryDialog) {
                    MoveToLibraryDialog(
                        onConfirm = { bookcaseNumber, shelfNumber ->
                            viewModel.moveToLibrary(bookData, bookcaseNumber, shelfNumber)
                            navController.navigate(Screen.Library.route) {
                                popUpTo(Screen.Wishlist.route)
                            }
                        },
                        onDismiss = { showMoveToLibraryDialog = false }
                    )
                }
            } ?: run {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun WishlistDetailContent(book: WishlistBook) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Book cover
        AsyncImage(
            model = book.coverUrl,
            contentDescription = book.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Fit
        )

        // Title
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineMedium
        )

        // Author
        Text(
            text = "By ${book.author}",
            style = MaterialTheme.typography.titleMedium
        )

        // Price
        book.price?.let { price ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$${price}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MoveToLibraryDialog(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var bookcaseNumber by remember { mutableStateOf("") }
    var shelfNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Library") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Enter the location for the book:")
                OutlinedTextField(
                    value = bookcaseNumber,
                    onValueChange = { bookcaseNumber = it },
                    label = { Text("Bookcase Number") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = shelfNumber,
                    onValueChange = { shelfNumber = it },
                    label = { Text("Shelf Number") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val bookcase = bookcaseNumber.toIntOrNull()
                    val shelf = shelfNumber.toIntOrNull()
                    if (bookcase != null && shelf != null) {
                        onConfirm(bookcase, shelf)
                    }
                },
                enabled = bookcaseNumber.isNotEmpty() && shelfNumber.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
