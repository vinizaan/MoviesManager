package com.github.vinizaan.moviesmanager.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Movie (
    @PrimaryKey
    var time: Long = INVALID_TIME,
    val name: String = "",
    var releaseYear: Int = 0,
    var studio: String = "",
    var durationMinutes: Int = 0,
    var movieWatched: Int = MOVIE_WATCHED_FALSE,
    var userRating: Float = 0f,
    var genre: String = "",
    ): Parcelable {
    companion object {
        const val INVALID_TIME = -1L
        const val MOVIE_WATCHED_TRUE = 1
        const val MOVIE_WATCHED_FALSE = 0
    }
}