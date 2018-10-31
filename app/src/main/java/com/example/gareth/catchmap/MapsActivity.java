package com.example.gareth.catchmap;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    //error dialog ID for creating permission dialog
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //constants to hold the location permission values
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false; //bool to determine if permission granted or not
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234; //code to permit access for location

    //Location variables
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;

    //GoogleMap variable to hold the map
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15f;

    //Button for open Add_catch_photo
    Button addCatch;

    //Instance of the ViewModel
    private CatchViewModel catchViewModel;

    List<Catch> catchHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //View model stuff
        //Initialize viewmodel
        catchViewModel = ViewModelProviders.of(this).get(CatchViewModel.class);

        //Create observer of the viewModel and take data that is needed
        catchViewModel.getAllCatches().observe(this, new Observer<List<Catch>>() {

            //onChanged is called whenever data is changed throughout the app
            @Override
            public void onChanged(@Nullable List<Catch> catches) {
                Toast.makeText(MapsActivity.this, "OnChanged Called", Toast.LENGTH_SHORT).show();

                catchHolder = catches;

                for(Catch c: catchHolder)
                {
                    LatLng catchLatLng = new LatLng(c.getLatitude(),c.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(catchLatLng).title(c.getFishType()));
                }
            }
        });

        if(isServicesOK()) //Check if google play service is installed/up to date
        {
           //Checks and gets permissions and initializes map if permissions are granted
           getLocationPermission();


            addCatch = findViewById(R.id.add_catch_butt);

            addCatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), add_catch_photo.class);
                    intent.putExtra("latitude", (float)currentLocation.getLatitude());
                    intent.putExtra("longitude", (float)currentLocation.getLongitude());
                    startActivity(intent);
                }
            });
        }
        else{
            Toast.makeText(this, "User permissions failed", Toast.LENGTH_SHORT).show();
        }
        
        DBhelper = new CatchDB(this);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting current device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                        }
                        else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Unable to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: Initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: Moving camera to : Lat: " + latLng.latitude +
        ", lng: " + latLng.longitude);
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , zoom) );
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if(mLocationPermissionGranted){
            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }


        //Listener for the markers.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Fragment frag = new display_catch();
                //bundle to pass variables with
                Bundle bundle = new Bundle();

                //send bundle to frag with all details of specific catch object

                //Check each catch to see if it has the same lat/long as the selected marker
                for(Catch c : catchHolder)
                {
                    LatLng catchLatLong = new LatLng(c.getLatitude(), c.getLongitude());

                    if(catchLatLong.latitude == marker.getPosition().latitude &&
                            catchLatLong.longitude == marker.getPosition().longitude)
                    {
                        bundle.putString("type", c.getFishType());
                        bundle.putFloat("length", c.getFishLength());
                        bundle.putFloat("weight", c.getFishWeight());
                        bundle.putString("description", c.getDesc());
                        bundle.putFloat("latitude", c.getLatitude());
                        bundle.putFloat("longitude", c.getLongitude());
                        bundle.putByteArray("photo", c.getPhoto());

                        Toast.makeText(MapsActivity.this, c.getFishType(), Toast.LENGTH_SHORT).show();
                    }
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                frag.setArguments(bundle);
                transaction.replace(R.id.map, frag);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            }
        });
    }

    //Checks googleplay services
    private boolean isServicesOK(){
        Log.d(TAG, "isServerceOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything okay, user can make map requests
            Log.d(TAG, "isServerceOK: Google play services working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but can be resolved
            Log.d(TAG, "isServerceOK: fixable error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this,
                    available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Cant make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //Explicitly check if the permissions are granted on device, if they are not, ask for permissions
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
        FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else {
                //Ask for permissions
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            //Ask for permissions
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //When a permission results comes back check if they are true or not.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called");
        mLocationPermissionGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permissions granted");
                    mLocationPermissionGranted = true;
                    initMap();
                }

            }
        }
    }

}
