package com.golriz.gpstracker.DB.db

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Database
import androidx.room.RoomDatabase
import com.golriz.gpstracker.DB.dao.UserLocationDao


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class UserLocationRoomDB : RoomDatabase() {

    abstract fun daoAccess(): UserLocationDao
}