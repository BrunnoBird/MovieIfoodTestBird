package com.example.movieifoodtest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteMovieEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
