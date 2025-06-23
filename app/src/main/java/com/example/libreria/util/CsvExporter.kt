package com.example.libreria.util

import com.example.libreria.data.model.Book
import java.io.File
import java.io.FileWriter

object CsvExporter {
    fun exportBooksToCsv(books: List<Book>, file: File, separator: Char = '\t') {
        FileWriter(file).use { writer ->
            writer.appendLine(listOf("ISBN", "Título", "Autor", "Editorial", "Páginas", "Precio", "Ubicación", "Fecha", "Sinopsis").joinToString(separator.toString()))
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
                ).joinToString(separator.toString()) { it.replace(separator.toString(), " ") })
            }
        }
    }
}
