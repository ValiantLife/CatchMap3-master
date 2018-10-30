package com.example.gareth.catchmap;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class CatchRepository {
    private CatchDao catchDao;
    private LiveData<List<Catch>> allCatches;

    public CatchRepository(Application application) {
        CatchDatabase database = CatchDatabase.getInstance(application);
        catchDao = database.catchDao();
        allCatches = catchDao.getAllCatches();
    }

    //Method bodies for database operations need to be hand written
    public void insert(Catch iCatch){
        new InsertCatchAsyncTask(catchDao).execute(iCatch);
    }

    public void update(Catch iCatch){
        new UpdateCatchAsyncTask(catchDao).execute(iCatch);
    }

    public void delete(Catch iCatch){
        new DeleteCatchAsyncTask(catchDao).execute(iCatch);
    }

    public LiveData<List<Catch>> getAllCatches() {
        return allCatches;
    }

    //Each database operation needs its own static class
    private static class InsertCatchAsyncTask extends AsyncTask<Catch, Void, Void> {
        private CatchDao catchDao;

        private InsertCatchAsyncTask(CatchDao catchDao){
            this.catchDao = catchDao;
        }

        @Override
        protected Void doInBackground(Catch... catches) {
            catchDao.insert(catches[0]);
            return null;
        }
    }

    private static class UpdateCatchAsyncTask extends AsyncTask<Catch, Void, Void> {
        private CatchDao catchDao;

        private UpdateCatchAsyncTask(CatchDao catchDao){
            this.catchDao = catchDao;
        }

        @Override
        protected Void doInBackground(Catch... catches) {
            catchDao.update(catches[0]);
            return null;
        }
    }

    private static class DeleteCatchAsyncTask extends AsyncTask<Catch, Void, Void> {
        private CatchDao catchDao;

        private DeleteCatchAsyncTask(CatchDao catchDao){
            this.catchDao = catchDao;
        }

        @Override
        protected Void doInBackground(Catch... catches) {
            catchDao.delete(catches[0]);
            return null;
        }
    }
}
