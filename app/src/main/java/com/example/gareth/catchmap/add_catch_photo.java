package com.example.gareth.catchmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ImageView;

public class add_catch_photo extends FragmentActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    float lat;
    float lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_catch_photo);

        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //This method only passes on the thumbnail image of the taken image,
    //Look at: https://developer.android.com/training/camera/photobasics
    //for details on how to send the full resolution image.
    //When sending the full resolution image, it may be best to sent a
    //URI to fragment for better efficiency. The database will most likely
    //hold the image as a URI also.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Bundle bundleExtras = getIntent().getExtras();
            lat = bundleExtras.getFloat("latitude");
            lng = bundleExtras.getFloat("longitude");

            ImageView mImageView = findViewById(R.id.camera_container);

            //Add bitmap to a bundle
            Bundle b = new Bundle();
            b.putParcelable("image", imageBitmap);
            b.putFloat("latitude", lat);
            b.putFloat("longitude", lng);


            //Create an instance of the fragment
            Fragment frag = new Add_catch_details();

            //Add bundle to fragment arguments
            frag.setArguments(b);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.details_container, frag);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();

        }
    }
}
