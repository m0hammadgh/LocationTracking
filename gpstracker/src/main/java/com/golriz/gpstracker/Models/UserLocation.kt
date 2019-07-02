package com.golriz.gpstracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userLocation")
class UserLocation {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var uid: Int = 0

    @ColumnInfo(name = "latitude")
    var latitude: Double? = null
    @ColumnInfo(name = "longtitude")
    var longtitude: Double? = null


    @ColumnInfo(name = "isSynced")
    var isSynced: Boolean = false

    @ColumnInfo(name = "time")
    var time: Long? = null
}