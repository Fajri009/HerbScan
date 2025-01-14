package com.example.herbscan.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntity::class], version = 1)
abstract class HerbScanDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HerbScanDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HerbScanDatabase {
            if (INSTANCE == null) {
                synchronized(HerbScanDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        HerbScanDatabase::class.java, "herbscan_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as HerbScanDatabase
        }
    }
}