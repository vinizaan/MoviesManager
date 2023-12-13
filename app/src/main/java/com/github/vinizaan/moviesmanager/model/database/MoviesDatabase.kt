package com.github.vinizaan.moviesmanager.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.vinizaan.moviesmanager.model.dao.TaskDao
import com.github.vinizaan.moviesmanager.model.entity.Movie

@Database(entities = [Movie::class], version = 1)
abstract class MovieListDatabase: RoomDatabase() {
    companion object {
        const val TO_DO_LIST_DATABASE = "toDoListDatabase"
    }
    abstract fun getTaskDao(): TaskDao
}