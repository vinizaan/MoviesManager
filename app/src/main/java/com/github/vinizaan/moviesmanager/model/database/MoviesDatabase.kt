package com.github.vinizaan.moviesmanager.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.vinizaan.moviesmanager.model.dao.MovieDao
import com.github.vinizaan.moviesmanager.model.entity.Movie

@Database(entities = [Movie::class], version = 1)
abstract class MoviesDatabase: RoomDatabase() {
    companion object {
        const val MOVIES_DATABASE = "moviesDatabase"
    }
    abstract fun getMovieDao(): MovieDao
}