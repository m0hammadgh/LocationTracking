package com.golriz.gpstracker.DB.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.golriz.gpstracker.DB.dao.UserLocationDao
import com.golriz.gpstracker.Models.UserLocation


@Database(entities = [UserLocation::class], version = 1, exportSchema = false)
abstract class UserLocationRoomDB : RoomDatabase() {

    abstract fun daoAccess(): UserLocationDao
}