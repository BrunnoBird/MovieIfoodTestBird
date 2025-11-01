package com.example.movieifoodtest.core.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val rating: Double
)
