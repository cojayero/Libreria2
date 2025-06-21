package com.example.libreria.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object CoverImageStorage {
    fun saveCoverImage(context: Context, isbn: String, bitmap: Bitmap): String? {
        val fileName = "cover_$isbn.jpg"
        val dir = File(context.filesDir, "covers")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        return try {
            val out = FileOutputStream(file)
            // Comprimir a JPEG, calidad 80, tamaño máximo 400x600 px
            val scaled = Bitmap.createScaledBitmap(bitmap, 400, 600, true)
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun loadCoverImage(context: Context, isbn: String): Bitmap? {
        val file = File(context.filesDir, "covers/cover_$isbn.jpg")
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }
}
