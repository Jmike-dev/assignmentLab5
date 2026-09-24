package com.example.assignmentlab5.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// @Dao is the list of questions you're allowed to ask the database.
// Room checks the SQL at compile time, not at crash time.
@Dao
interface NoteDao {

    // Flow = live results, not a one-time snapshot.
    // Any insert/delete/update anywhere will cause collectors of this to re-emit.
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun observeAll(): Flow<List<Note>>

    @Insert
    suspend fun add(note: Note)

    @Delete
    suspend fun remove(note: Note)

    // Room matches the row by primary key (id) and overwrites the rest
    // of the columns — this is what toggleNote() in the ViewModel calls.
    @Update
    suspend fun update(note: Note)
}