package com.example.libreria.di

import android.content.Context
import androidx.room.Room
import com.example.libreria.data.local.BookDao
import com.example.libreria.data.local.LibreriaDatabase
import com.example.libreria.data.local.WishlistDao
import com.example.libreria.data.remote.GoogleBooksApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideLibreriaDatabase(
        @ApplicationContext context: Context
    ): LibreriaDatabase = Room.databaseBuilder(
        context,
        LibreriaDatabase::class.java,
        LibreriaDatabase.DATABASE_NAME
    )
        .addMigrations(LibreriaDatabase.MIGRATION_1_2)
        .build()
    
    @Provides
    @Singleton
    fun provideBookDao(db: LibreriaDatabase): BookDao = db.bookDao()
    
    @Provides
    @Singleton
    fun provideWishlistDao(db: LibreriaDatabase): WishlistDao = db.wishlistDao()
    
    @Provides
    @Singleton
    fun provideGoogleBooksApi(): GoogleBooksApi {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)
    }
}
