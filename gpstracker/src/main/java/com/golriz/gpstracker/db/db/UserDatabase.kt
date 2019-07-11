package com.golriz.gpstracker.db.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.golriz.gpstracker.db.dao.UserLocationDao
import com.golriz.gpstracker.db.model.UserCurrentLocation


@Database(entities = [UserCurrentLocation::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun daoAccess(): UserLocationDao
}
