package com.example.libreria.util

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val PREFS_NAME = "libreria_prefs"
    private const val KEY_BOOKCASE = "default_bookcase"
    private const val KEY_SHELF = "default_shelf"

    fun setDefaultLocation(context: Context, bookcase: String, shelf: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BOOKCASE, bookcase).putString(KEY_SHELF, shelf).apply()
    }

    fun getDefaultBookcase(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BOOKCASE, null)
    }

    fun getDefaultShelf(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SHELF, null)
    }
}
