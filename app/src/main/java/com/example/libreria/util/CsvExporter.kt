package com.example.libreria.util

import com.example.libreria.data.model.Book
import java.io.File
import java.io.FileWriter

object CsvExporter {
    fun exportBooksToCsv(books: List<Book>, file: File) {
        FileWriter(file).use { writer ->
            writer.appendLine("ISBN,Título,Autor,Editorial,Páginas,Precio,Ubicación,Fecha,Sinopsis")
            for (book in books) {
                writer.appendLine(listOf(
                    book.isbn,
                    book.title,
                    book.author,
                    book.editorial ?: "",
                    book.pageCount?.toString() ?: "",
                    book.price?.toString() ?: "",
                    "${book.bookcaseNumber ?: ""}-${book.shelfNumber ?: ""}",
                    book.addedDate.toString(),
                    book.synopsis?.replace("\n", " ") ?: ""
                ).joinToString(",") { it.replace(",", " ") })
            }
        }
    }
}
