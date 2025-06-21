package com.example.libreria.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.libreria.R
import com.example.libreria.data.model.Book
import com.example.libreria.util.AppPreferences
import com.example.libreria.util.CoverImageStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    isbn: String,
    navController: NavController,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    val book = remember(isbn) { viewModel.getBook(isbn) }.collectAsState(initial = null)
    val context = LocalContext.current
    var customCoverPath by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val path = CoverImageStorage.saveCoverImage(context, isbn, bitmap)
            customCoverPath = path
            viewModel.updateBookCoverPath(isbn, path)
            Toast.makeText(context, "Carátula guardada correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
                BookDetailContent(
                    book = bookData,
                    onEdit = { navController.navigate("editBook/${bookData.isbn}") },
                    onDelete = {
                        viewModel.deleteBook(bookData) {
                            navController.popBackStack()
                        }
                    },
                    onTakeCoverPhoto = {
                        cameraLauncher.launch(null)
                    },
                    customCoverPath = customCoverPath
                )

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
private fun BookDetailContent(
    book: Book,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onTakeCoverPhoto: () -> Unit = {},
    customCoverPath: String? = null
) {
    val context = LocalContext.current
    val defaultBookcase = AppPreferences.getDefaultBookcase(context)
    val defaultShelf = AppPreferences.getDefaultShelf(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Book cover
        if (customCoverPath != null) {
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(customCoverPath),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit
            )
        } else if (book.coverUrl != null) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(id = R.drawable.ic_book_placeholder),
                error = painterResource(id = R.drawable.ic_book_placeholder)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onTakeCoverPhoto) {
                    Icon(Icons.Default.Camera, contentDescription = "Tomar foto de carátula")
                }
                Text("Tomar foto de la carátula", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onEdit) {
                Text("Editar ficha")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Borrar libro")
            }
        }

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
        } else if (!defaultBookcase.isNullOrBlank() && !defaultShelf.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ubicación por defecto",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Estantería: $defaultBookcase",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Repisa: $defaultShelf",
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
                        text = "Precio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "€${"%.2f".format(price)}",
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
