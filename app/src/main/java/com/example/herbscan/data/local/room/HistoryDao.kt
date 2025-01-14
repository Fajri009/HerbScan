package com.example.herbscan.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHistory(history: HistoryEntity)

    @Query("SELECT * FROM history WHERE LOWER(plantName) LIKE LOWER('%' || :namePlant || '%') AND userId = :userId ORDER BY date DESC")
    fun getHistoryByName(userId: String, namePlant: String): LiveData<List<HistoryEntity>>
}