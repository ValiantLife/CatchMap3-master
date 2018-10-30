package com.example.gareth.catchmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class display_catch extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View to hold the current views inflater
        View view = inflater.inflate(R.layout.display_catch_details, container, false);

        //Create TextView instances and assign bundle varialbe to the textview's
        //Fish type
        TextView typeText = view.findViewById(R.id.txt_fish_type);
        typeText.setText(getArguments().getString("type"));

        //Fish weight
        TextView weightText = view.findViewById(R.id.txt_fish_weight);
        String weight = Float.toString(getArguments().getFloat("weight"));
        weightText.setText(weight);

        //Fish length
        TextView lengthText = view.findViewById(R.id.txt_fish_length);
        String length = Float.toString(getArguments().getFloat("length"));
        lengthText.setText(length);

        //Description
        TextView typeDesc = view.findViewById(R.id.txt_fish_desc);
        typeDesc.setText(getArguments().getString("description"));

        //Fish lat
        TextView lengthLat = view.findViewById(R.id.txt_fish_lat);
        String lat = Float.toString(getArguments().getFloat("latitude"));
        lengthLat.setText(lat);

        //Fish lng
        TextView lengthLng = view.findViewById(R.id.txt_fish_long);
        String lng = Float.toString(getArguments().getFloat("longitude"));
        lengthLng.setText(lng);

        //Add image to the fragment
        ImageView imgView = view.findViewById(R.id.image_container);
        byte[] bitmapdata = getArguments().getByteArray("photo");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        imgView.setImageBitmap(bitmap);

        return view;
    }
}
