package com.github.vinizaan.moviesmanager.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.vinizaan.moviesmanager.model.entity.Movie

@Dao
interface TaskDao {
    companion object {
        const val TASK_TABLE = "task"
    }
    @Insert
    fun createTask(movie: Movie)
    @Query("SELECT * FROM $TASK_TABLE")
    fun retrieveTasks(): List<Movie>
    @Update
    fun updateTask(movie: Movie)
    @Delete
    fun deleteTask(movie: Movie)
}