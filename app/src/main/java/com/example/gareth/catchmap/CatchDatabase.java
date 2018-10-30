package com.example.gareth.catchmap;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Catch.class}, version = 2)
public abstract class CatchDatabase extends RoomDatabase {

    private static CatchDatabase instance;

    //Room will automatically create the body for this function
    //as I have built the database
    public abstract CatchDao catchDao();

    public static synchronized CatchDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CatchDatabase.class, "catch_database")
                    .fallbackToDestructiveMigration() //SQLite requires a migration method as a new
                                                      //instance of the database is created each version (migrate previous database to new version)
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private CatchDao catchDao;

        private PopulateDbAsyncTask(CatchDatabase db) {
            catchDao = db.catchDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

          /*  catchDao.insert(new Catch("Due Fish", (float)1.6, (float)5.2,
                    "Used squid", (float)-32.1224840, (float)115.8518331, ""));

            catchDao.insert(new Catch("Test", (float)2.3, (float)10,
                    "Used squid", (float)-32.1224961, (float)115.6518331, ""));

            catchDao.insert(new Catch("Snapper", (float)2.3, (float)10,
                    "Used squid", (float)-32.1224961, (float)115.7518331, ""));*/

            return null;
        }
    }
}
