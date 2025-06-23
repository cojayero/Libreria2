package com.example.libreria.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CloudUpload
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.io.OutputStream
import com.example.libreria.data.model.WishlistBook
import com.example.libreria.ui.wishlist.WishlistViewModel

import com.example.libreria.data.model.Book
import com.example.libreria.ui.navigation.Screen

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val wishlistState by wishlistViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var filter by remember { mutableStateOf("Todos") }

    // Launcher para crear documento en Drive (formato CSV)
    val createDocumentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportBooksToCsv { csvFile ->
                try {
                    val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                    csvFile.inputStream().use { input ->
                        outputStream?.use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val wishlistIsbns = (wishlistState as? com.example.libreria.ui.wishlist.WishlistUiState.Success)?.books?.map { it.isbn }?.toSet() ?: emptySet()
    val wishlistBooks = (wishlistState as? com.example.libreria.ui.wishlist.WishlistUiState.Success)?.books ?: emptyList()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                FilterChip(selected = filter == "Todos", onClick = { filter = "Todos" }, label = { Text("Todos") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = filter == "Biblioteca", onClick = { filter = "Biblioteca" }, label = { Text("Biblioteca") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = filter == "Wishlist", onClick = { filter = "Wishlist" }, label = { Text("Wishlist") })
            }
            Row {
                IconButton(onClick = {
                    viewModel.exportBooksToCsv { csvFile ->
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            context.packageName + ".provider",
                            csvFile
                        )
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Exportación de libros")
                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(
                            android.content.Intent.createChooser(intent, "Enviar base de datos por email")
                        )
                    }
                }) {
                    Icon(Icons.Default.Email, contentDescription = "Exportar CSV por Email")
                }
                IconButton(onClick = {
                    // Lanzar selector para guardar en Drive como CSV (tabulador)
                    createDocumentLauncher.launch("libreria.csv")
                }) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Guardar en Google Drive (CSV)")
                }
            }
        }
        when (val state = uiState) {
            is LibraryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LibraryUiState.Success -> {
                val books = when (filter) {
                    "Todos" -> state.books + wishlistBooks.filter { w -> state.books.none { it.isbn == w.isbn } }
                    "Biblioteca" -> state.books
                    "Wishlist" -> wishlistBooks.filter { w -> state.books.none { it.isbn == w.isbn } }
                    else -> state.books
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(books) { book ->
                        val isbn = when (book) {
                            is Book -> book.isbn
                            is WishlistBook -> book.isbn
                            else -> ""
                        }
                        val isWishlist = wishlistIsbns.contains(isbn)
                        BookCard(
                            book = book,
                            isWishlist = isWishlist,
                            onClick = { navController.navigate(Screen.BookDetail.createRoute(isbn)) }
                        )
                    }
                }
            }
            is LibraryUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(
    book: Any, // Puede ser Book o WishlistBook
    isWishlist: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            val coverUrl = when (book) {
                is Book -> book.coverUrl
                is WishlistBook -> book.coverUrl
                else -> null
            }
            val title = when (book) {
                is Book -> book.title
                is WishlistBook -> book.title
                else -> ""
            }
            val author = when (book) {
                is Book -> book.author
                is WishlistBook -> book.author
                else -> ""
            }
            AsyncImage(
                model = coverUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (isWishlist) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Wishlist",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
