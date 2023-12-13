package com.github.vinizaan.moviesmanager.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Task (
    @PrimaryKey
    var time: Long = INVALID_TIME,
    var name: String = "",
    var done: Int = TASK_DONE_FALSE
): Parcelable {
    companion object {
        const val INVALID_TIME = -1L
        const val TASK_DONE_TRUE = 1
        const val TASK_DONE_FALSE = 0
    }
}