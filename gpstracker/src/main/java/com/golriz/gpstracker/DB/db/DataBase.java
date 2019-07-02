package com.golriz.gpstracker.DB.db;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.golriz.gpstracker.DB.dao.UserLocationDao;
import com.golriz.gpstracker.Models.UserLocation;

@Database(entities = {UserLocation.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    private static DataBase instance;
    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            new PopulateDbAsynTask(instance).execute();
            super.onCreate(db);
        }
    };
    private static Callback roomCallback2 = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DataBase.class, "userLocation_database")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract UserLocationDao noteDao();

    private static class PopulateDbAsynTask extends AsyncTask<Void, Void, Void> {
        private UserLocationDao noteDao;

        public PopulateDbAsynTask(DataBase noteDataBase) {
            noteDao = noteDataBase.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UserLocation userLocation = new UserLocation();
            userLocation.setSynced(false);
            userLocation.setTime(1562051531L);
            noteDao.insetLocation(userLocation);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
