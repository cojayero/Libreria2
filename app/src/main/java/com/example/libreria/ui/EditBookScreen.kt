package com.example.libreria.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.libreria.data.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    isbn: String,
    navController: NavController,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val book = remember(isbn) { viewModel.getBook(isbn) }.collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var editorial by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var synopsis by remember { mutableStateOf("") }

    LaunchedEffect(book.value) {
        book.value?.let {
            title = it.title
            author = it.author
            editorial = it.editorial ?: ""
            pageCount = it.pageCount?.toString() ?: ""
            synopsis = it.synopsis ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar libro") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editorial,
                onValueChange = { editorial = it },
                label = { Text("Editorial") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pageCount,
                onValueChange = { pageCount = it },
                label = { Text("Páginas") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = synopsis,
                onValueChange = { synopsis = it },
                label = { Text("Sinopsis") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val updatedBook = book.value?.copy(
                        title = title,
                        author = author,
                        editorial = editorial,
                        pageCount = pageCount.toIntOrNull(),
                        synopsis = synopsis
                    )
                    if (updatedBook != null) {
                        viewModel.addOrUpdateBook(updatedBook) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && author.isNotBlank()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}
