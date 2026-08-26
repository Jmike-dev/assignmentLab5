package com.example.assignmentlab5.data

import android.content.Context


import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

// @Database lists every entity it manages and hands out the DAO(s).
@Database(entities = [Note::class], version = 1)
abstract class NoteDb : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        // Volatile ensures writes to INSTANCE are visible to all threads immediately.
        @Volatile
        private var INSTANCE: NoteDb? = null

        // Standard singleton pattern so we never open more than one connection
        // to the same database file.
        fun getInstance(context: Context): NoteDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDb::class.java,
                    "note_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}