package com.golriz.gpstracker.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.golriz.gpstracker.db.model.UserCurrentLocation


@Dao
interface UserLocationDao {

    @Query("SELECT * FROM userLocation")
    fun getAllLocations(): List<UserCurrentLocation>

    @Query("SELECT * FROM userLocation Where isSynced = '0'")
    fun getUnSyncedLocation(): List<UserCurrentLocation>


    @Insert
    fun insetLocation(userLocation: UserCurrentLocation)

    @Query("SELECT * FROM userLocation WHERE isSynced = :status order By id ASC LIMIT :count")
    fun selectNumberOfLocations(status: Boolean, count: Int): List<UserCurrentLocation>

    @Update
    fun updateLocation(userLocation: UserCurrentLocation)


    @Query("select * from userLocation where isSynced='0' order by id desc limit 1")
    fun getLastItem(): LiveData<UserCurrentLocation>

    @Query("select * from userLocation where isSynced='0' order by id desc limit 1")
    fun findByUserId(): UserCurrentLocation


}