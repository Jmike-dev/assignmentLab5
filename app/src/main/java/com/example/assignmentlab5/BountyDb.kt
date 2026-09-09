package com.example.assignmentlab5.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Bounty::class], version = 1)
@TypeConverters(TagsConverter::class)
abstract class BountyDb : RoomDatabase() {

    abstract fun bountyDao(): BountyDao

    companion object {
        @Volatile
        private var INSTANCE: BountyDb? = null

        fun getInstance(context: Context): BountyDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BountyDb::class.java,
                    "bounty_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed database on first launch
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.bountyDao().insertAll(sampleBounties)
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
        }

        private val sampleBounties = listOf(
            Bounty(title = "Slay the Goblin Chieftain", tags = listOf("Combat", "Boss"), rewardGold = 150, progress = 0.2f),
            Bounty(title = "Infiltrate Royal Vault", tags = listOf("Stealth", "Rogue", "Escort"), rewardGold = 500, progress = 0.5f),
            Bounty(title = "Fetch Eldritch Herbs", tags = listOf("Fetch", "Alchemy", "Casual"), rewardGold = 50, progress = 0.8f),
            Bounty(title = "Escort Merchant Caravan", tags = listOf("Escort", "Combat", "Defense", "Long Distance"), rewardGold = 300, progress = 0.0f)
        )
    }
}