package com.example.libreria.ui.bookdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.libreria.ui.BookDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun BookDetailScreen(
    isbn: String,
    navController: NavController,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val book by viewModel.getBook(isbn).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        book?.let { bookInfo ->
            Text(
                text = bookInfo.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "By ${bookInfo.author}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Location:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Bookcase ${bookInfo.bookcaseNumber}, Shelf ${bookInfo.shelfNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = {
                    // TODO: Add edit location functionality
                }
            ) {
                Text("Edit Location")
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}
