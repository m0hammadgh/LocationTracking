package com.golriz.gpstracker.DB.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.golriz.gpstracker.DB.db.UserDatabase
import com.golriz.gpstracker.DB.model.UserCurrentLocation

class RoomRepository(context: Context) {

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

    fun getUnSyncedLocations(count: Int) {
        val tasks: LiveData<List<UserCurrentLocation>>
        noteDatabase.daoAccess().selectNumberOfLocations(false, count)
    }

    fun getLasSubmittedRecord(): LiveData<UserCurrentLocation> {
        return noteDatabase.daoAccess().getLastItem()
    }


}
