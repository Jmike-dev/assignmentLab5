package com.example.assignmentlab5.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BountyDao {
    @Query("SELECT * FROM bounties ORDER BY id ASC")
    fun observeAll(): Flow<List<Bounty>>

    @Insert
    suspend fun insertAll(bounties: List<Bounty>): List<Long> // Return List<Long> instead of Unit

    @Update
    suspend fun update(bounty: Bounty): Int // Return Int (rows updated) instead of Unit

    @Delete
    suspend fun delete(bounty: Bounty): Int // Return Int (rows deleted) instead of Unit

    @Query("SELECT COUNT(*) FROM bounties")
    suspend fun getCount(): Int
}