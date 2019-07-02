package com.golriz.gpstracker.DB.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.golriz.gpstracker.DB.db.NoteDatabase
import com.golriz.gpstracker.DB.model.UserCurrentLocation

class NoteRepository(context: Context) {

    private val DB_NAME = "db_task"

    private val noteDatabase: NoteDatabase

    //
    //    public LiveData<UserCurrentLocation> getTask(int id) {
    //        return noteDatabase.daoAccess().get(id);
    //    }

    val tasks: LiveData<List<UserCurrentLocation>>
        get() = noteDatabase.daoAccess().allLocation

    init {
        noteDatabase = Room.databaseBuilder(context, NoteDatabase::class.java, DB_NAME).build()
    }


    fun insertTask(
        latitude: Double?,
        longtitude: Double?
    ) {

        val note = UserCurrentLocation()
        note.latitude = latitude
        note.longtitude = longtitude
        note.isSynced = false
        note.timeStamp = 2145545L


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
        noteDatabase.daoAccess().selectNumberOfLocations(false, 10)
    }
    //
    //    public void updateTask(final UserCurrentLocation note) {
    //        note.setModifiedAt(AppUtils.getCurrentDateTime());
    //
    //        new AsyncTask<Void, Void, Void>() {
    //            @Override
    //            protected Void doInBackground(Void... voids) {
    //                noteDatabase.daoAccess().updateTask(note);
    //                return null;
    //            }
    //        }.execute();
    //    }
    //
    //    public void deleteTask(final int id) {
    //        final LiveData<UserCurrentLocation> task = getTask(id);
    //        if (task != null) {
    //            new AsyncTask<Void, Void, Void>() {
    //                @Override
    //                protected Void doInBackground(Void... voids) {
    //                    noteDatabase.daoAccess().deleteTask(task.getValue());
    //                    return null;
    //                }
    //            }.execute();
    //        }
    //    }
    //
    //    public void deleteTask(final UserCurrentLocation note) {
    //        new AsyncTask<Void, Void, Void>() {
    //            @Override
    //            protected Void doInBackground(Void... voids) {
    //                noteDatabase.daoAccess().deleteTask(note);
    //                return null;
    //            }
    //        }.execute();
    //    }

}
