package com.example.herbscan.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "userId")
    val userId: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "plantName")
    var plantName: String,

    @ColumnInfo(name = "plantScientificName")
    var plantScientificName: String,

    @ColumnInfo(name = "accuracy")
    var accuracy: String,

)