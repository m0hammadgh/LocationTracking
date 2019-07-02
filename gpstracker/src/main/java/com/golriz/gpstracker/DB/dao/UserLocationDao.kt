package com.golriz.gpstracker.DB.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.golriz.gpstracker.Models.UserLocation


@Dao
interface UserLocationDao {

    @get:Query("SELECT * FROM userLocation")
    val allLocation: LiveData<List<UserLocation>>

    @Query("SELECT * FROM userLocation Where isSynced = 'false'")
    fun getUnSyncedLocation(): LiveData<List<UserLocation>>

    @Insert
    fun insetLocation(userLocation: UserLocation)

    @Query("SELECT * FROM userLocation WHERE isSynced = :status order By id ASC LIMIT :count")
    fun selectNumberOfLocations(status: Boolean, count: Int): List<UserLocation>

    @Update
    fun updateLocation(userLocation: UserLocation)
}