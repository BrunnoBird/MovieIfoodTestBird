package com.example.movieifoodtest.core.di

import androidx.room.Room
import com.example.movieifoodtest.core.database.AppDatabase
import org.koin.dsl.module

private const val DATABASE_NAME = "movies.db"

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
    single { get<AppDatabase>().favoriteDao() }
}
