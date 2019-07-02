package com.golriz.gpstracker.DB.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import com.golriz.gpstracker.DB.db.DataBase;
import com.golriz.gpstracker.Models.UserLocation;

import java.util.ArrayList;
import java.util.List;

public class UserLocationRepository {

    private String DB_NAME = "userLocation_db";

    private DataBase noteDatabase;

    public UserLocationRepository(Context context) {
        noteDatabase = Room.databaseBuilder(context, DataBase.class, DB_NAME).build();
    }

    @SuppressLint("StaticFieldLeak")

    public void insertLocation(final UserLocation location) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                noteDatabase.daoAccess().insetLocation(location);
                return null;
            }
        }.execute();
    }


    @SuppressLint("StaticFieldLeak")
    public void updateSyncedLocation(final ArrayList<UserLocation> userLocations) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (UserLocation u : userLocations) {
                    u.setSynced(true);
                    noteDatabase.daoAccess().updateLocation(u);
                }

                return null;
            }
        }.execute();
    }

//    public void deleteTask(final int id) {
//        final LiveData<Note> task = getTask(id);
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
//    public void deleteTask(final Note note) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                noteDatabase.daoAccess().deleteTask(note);
//                return null;
//            }
//        }.execute();
//    }

    public LiveData<List<UserLocation>> getUnSyncedLocations() {
        return noteDatabase.daoAccess().getUnSyncedLocation();
    }

}
