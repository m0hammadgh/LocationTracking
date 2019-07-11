package com.golriz.gpstracker.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable


@Entity(tableName = "userLocation")
class UserCurrentLocation : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "timeStamp")
    var timeStamp: Long? = null

    @ColumnInfo(name = "latitude")
    var latitude: Double? = null

    @ColumnInfo(name = "longtitude")
    var longtitude: Double? = null

    @ColumnInfo(name = "isSynced")
    var isSynced: Boolean = false
}
