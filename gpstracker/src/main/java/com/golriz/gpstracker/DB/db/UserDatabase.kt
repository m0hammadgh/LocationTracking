package com.golriz.gpstracker.DB.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.golriz.gpstracker.DB.dao.UserLocationDao
import com.golriz.gpstracker.DB.model.UserCurrentLocation


@Database(entities = [UserCurrentLocation::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun daoAccess(): UserLocationDao
}
