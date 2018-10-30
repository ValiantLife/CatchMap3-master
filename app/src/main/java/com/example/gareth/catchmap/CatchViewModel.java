package com.example.gareth.catchmap;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class CatchViewModel extends AndroidViewModel {

    private CatchRepository repository;
    private LiveData<List<Catch>> allCatches;


    public CatchViewModel(@NonNull Application application) {
        super(application);

        repository = new CatchRepository(application);
        allCatches = repository.getAllCatches();
    }

    public void insert(Catch iCatch) {
        repository.insert(iCatch);
    }

    public void update(Catch iCatch) {
        repository.update(iCatch);
    }

    public void delete(Catch iCatch) {
        repository.delete(iCatch);
    }

    public LiveData<List<Catch>> getAllCatches() {
        return allCatches;
    }
}
