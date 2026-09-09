package com.example.assignmentlab5.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "bounties")
data class Bounty(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val tags: List<String>,
    val rewardGold: Int,
    val progress: Float = 0f, // 0f to 1f
    val isAccepted: Boolean = false
)

class TagsConverter {
    @TypeConverter
    fun fromList(tags: List<String>): String = tags.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(",")
}