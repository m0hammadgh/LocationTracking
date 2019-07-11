package com.golriz.gpstracker.db.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.golriz.gpstracker.db.db.UserDatabase
import com.golriz.gpstracker.db.model.UserCurrentLocation
import com.golriz.gpstracker.utils.SharedPrefManager

class RoomRepository(private val context: Context) {

    private val DB_NAME = "db_task"

    private val noteDatabase: UserDatabase


    //
    //    public LiveData<UserCurrentLocation> getTask(int id) {
    //        return noteDatabase.daoAccess().get(id);
    //    }

    val tasks: LiveData<List<UserCurrentLocation>>
        get() = noteDatabase.daoAccess().allLocation

    init {
        noteDatabase = Room.databaseBuilder(context, UserDatabase::class.java, DB_NAME).build()
    }


    fun insertTask(
        latitude: Double?,
        longitude: Double?
    ) {

        val note = UserCurrentLocation()
        note.latitude = latitude
        note.longtitude = longitude
        note.isSynced = false
        val tsLong = System.currentTimeMillis() / 1000

        note.timeStamp = tsLong


        insertTask(note)
    }


    @SuppressLint("StaticFieldLeak")
    fun insertTask(note: UserCurrentLocation) {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                noteDatabase.daoAccess().insetLocation(note)
                return null
            }
        }.execute()
    }

    fun getUnSyncedLocations(count: Int): List<UserCurrentLocation> {
        return GetUnSyncedLocations().execute().get()
    }

    fun getLasSubmittedRecord(): LiveData<UserCurrentLocation> {
        checkPrePopulation()
        return noteDatabase.daoAccess().getLastItem()
    }

    fun getLasSubmittedItem(): UserCurrentLocation {
        checkPrePopulation()
        return GetNotesAsyncTask().execute().get()

    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetNotesAsyncTask : AsyncTask<Void, Void, UserCurrentLocation>() {
        override fun doInBackground(vararg voids: Void): UserCurrentLocation {
            return noteDatabase.daoAccess().findByUserId()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetUnSyncedLocations : AsyncTask<Void, Void, List<UserCurrentLocation>>() {
        override fun doInBackground(vararg p0: Void?): List<UserCurrentLocation> {
            return noteDatabase.daoAccess().getUnSyncedLocation()
        }

    }


    fun checkPrePopulation() {
        val appPreferences = SharedPrefManager(context)
        if (appPreferences.getIsPopulatedDb == false) {
            insertTask(0.0, 0.0)
            appPreferences.setDBPopulated(true)

        }
    }

}
