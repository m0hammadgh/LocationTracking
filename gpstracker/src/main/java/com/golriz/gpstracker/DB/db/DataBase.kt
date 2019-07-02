package com.golriz.gpstracker.DB.db

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.golriz.gpstracker.DB.dao.UserLocationDao
import com.golriz.gpstracker.Models.UserLocation

@Database(entities = [UserLocation::class], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun daoAccess(): UserLocationDao

    private class PopulateDbAsynTask(noteDataBase: DataBase) : AsyncTask<Void, Void, Void>() {
        private val noteDao: UserLocationDao

        init {
            noteDao = noteDataBase.daoAccess()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            val userLocation = UserLocation()
            userLocation.isSynced = false
            userLocation.time = 1562051531L
            noteDao.insetLocation(userLocation)


            return null
        }

    }

    companion object {

        private var instance: DataBase? = null
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                PopulateDbAsynTask(instance!!).execute()
                super.onCreate(db)
            }
        }
        private val roomCallback2 = object : RoomDatabase.Callback() {
        }

        @Synchronized
        fun getInstance(context: Context): DataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, DataBase::class.java, "userLocation_database")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance as DataBase
        }
    }

}
