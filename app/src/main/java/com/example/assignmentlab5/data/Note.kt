package com.example.assignmentlab5.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity turns this data class into a table.
// Each val below becomes a column.
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val createdAt: Long
)