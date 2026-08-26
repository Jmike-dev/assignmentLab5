package com.example.assignmentlab5.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

// @Dao is the list of questions you're allowed to ask the database.
// Room checks the SQL at compile time, not at crash time.
@Dao
interface NoteDao {

    // Flow = live results, not a one-time snapshot.
    // Any insert/delete anywhere will cause collectors of this to re-emit.
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun observeAll(): Flow<List<Note>>

    @Insert
    suspend fun add(note: Note)

    @Delete
    suspend fun remove(note: Note)
}