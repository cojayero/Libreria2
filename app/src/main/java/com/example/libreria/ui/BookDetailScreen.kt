package com.example.libreria.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    isbn: String,
    navController: NavController,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    val book = remember(isbn) { viewModel.getBook(isbn) }.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showLocationDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit location")
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
            book.value?.let { bookData ->
                BookDetailContent(book = bookData)
                
                if (showLocationDialog) {
                    LocationEditDialog(
                        currentBookcase = bookData.bookcaseNumber,
                        currentShelf = bookData.shelfNumber,
                        onConfirm = { bookcaseNumber, shelfNumber ->
                            viewModel.updateBookLocation(isbn, bookcaseNumber, shelfNumber)
                            showLocationDialog = false
                        },
                        onDismiss = { showLocationDialog = false }
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
private fun BookDetailContent(book: Book) {
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

        // ISBN
        Text(
            text = "ISBN: ${book.isbn}",
            style = MaterialTheme.typography.bodyMedium
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

        // Publisher (Editorial)
        Text(
            text = "Editorial: ${book.editorial ?: "No disponible"}",
            style = MaterialTheme.typography.bodyMedium
        )

        // Number of pages
        Text(
            text = "Páginas: ${book.pageCount?.toString() ?: "No disponible"}",
            style = MaterialTheme.typography.bodyMedium
        )

        // Location
        if (book.bookcaseNumber != null && book.shelfNumber != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Bookcase: ${book.bookcaseNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Shelf: ${book.shelfNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Synopsis
        if (book.synopsis != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = book.synopsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

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
private fun LocationEditDialog(
    currentBookcase: Int?,
    currentShelf: Int?,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var bookcaseNumber by remember { mutableStateOf(currentBookcase?.toString() ?: "") }
    var shelfNumber by remember { mutableStateOf(currentShelf?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Location") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
