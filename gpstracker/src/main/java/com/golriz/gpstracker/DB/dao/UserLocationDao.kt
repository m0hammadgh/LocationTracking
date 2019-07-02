package com.golriz.gpstracker.DB.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.golriz.gpstracker.DB.model.UserCurrentLocation


@Dao
interface UserLocationDao {

    @get:Query("SELECT * FROM userLocation")
    val allLocation: LiveData<List<UserCurrentLocation>>

    @Query("SELECT * FROM userLocation Where isSynced = 'false'")
    fun getUnSyncedLocation(): LiveData<List<UserCurrentLocation>>


    @Insert
    fun insetLocation(userLocation: UserCurrentLocation)

    @Query("SELECT * FROM userLocation WHERE isSynced = :status order By id ASC LIMIT :count")
    fun selectNumberOfLocations(status: Boolean, count: Int): List<UserCurrentLocation>

    @Update
    fun updateLocation(userLocation: UserCurrentLocation)
}