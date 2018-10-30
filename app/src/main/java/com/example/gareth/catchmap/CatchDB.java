package com.example.gareth.catchmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class CatchDB {

    CatchDBHelper mDbHelper;
    public CatchDB(Context context)
    {
        mDbHelper = new CatchDBHelper(context);
    }

    public long insertData(byte[] photo, String fishType, float fishLength, float fishWeight, String desc, float longitude, float latitude){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(mDbHelper.CATCH_COLUMN_PHOTO, photo);
        values.put(mDbHelper.CATCH_COLUMN_FISHTYPE, fishType);
        values.put(mDbHelper.CATCH_COLUMN_FISHLENGTH, fishLength);
        values.put(mDbHelper.CATCH_COLUMN_FISHWEIGHT, fishWeight);
        values.put(mDbHelper.CATCH_COLUMN_DESCRIPTION, desc);
        values.put(mDbHelper.CATCH_COLUMN_LONGITUDE, longitude);
        values.put(mDbHelper.CATCH_COLUMN_LATITUDE, latitude);

        long id = db.insert(mDbHelper.TABLE_NAME, null, values);
        return id;
    }

    public Catch[] getCatchObject() {


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] columns = {mDbHelper.CATCH_COLUMN_PHOTO, mDbHelper.CATCH_COLUMN_FISHTYPE, mDbHelper.CATCH_COLUMN_FISHLENGTH, mDbHelper.CATCH_COLUMN_FISHWEIGHT, mDbHelper.CATCH_COLUMN_DESCRIPTION, mDbHelper.CATCH_COLUMN_LONGITUDE, mDbHelper.CATCH_COLUMN_LATITUDE};
        Cursor cursor = db.query(mDbHelper.DATABASE_NAME, columns, null, null, null, null, null);
        int count = cursor.getCount();
        Catch[] catchListing;
        catchListing = new Catch[count];
        count = 0;
        while(cursor.moveToNext())
        {
            int cid = cursor.getInt(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_ID));
            byte[] cbyte = cursor.getBlob(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_PHOTO));
            String cType = cursor.getString(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_FISHTYPE));
            float cLength = cursor.getFloat(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_FISHLENGTH));
            float cWeight = cursor.getFloat(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_FISHWEIGHT));
            String cDesc = cursor.getString(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_DESCRIPTION));
            float cLong = cursor.getFloat(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_LONGITUDE));
            float cLat = cursor.getFloat(cursor.getColumnIndex(mDbHelper.CATCH_COLUMN_LATITUDE));

            catchListing[count] = new Catch(cbyte, cType, cLength, cWeight, cDesc, cLong, cLat);
            count++;


        }
        return catchListing;
    }








    static class CatchDBHelper extends SQLiteOpenHelper {

        public static final String TABLE_NAME = "catchDB";
        public static final String CATCH_COLUMN_ID = "id";
        public static final String CATCH_COLUMN_PHOTO = "photo";
        public static final String CATCH_COLUMN_FISHTYPE = "fishType";
        public static final String CATCH_COLUMN_FISHLENGTH = "fishLength";
        public static final String CATCH_COLUMN_FISHWEIGHT = "fishWeight";
        public static final String CATCH_COLUMN_DESCRIPTION = "description";
        public static final String CATCH_COLUMN_LONGITUDE = "longitude";
        public static final String CATCH_COLUMN_LATITUDE = "latitude";
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Catch.db";
        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME+ //Code to create the database
                " (" +CATCH_COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + //Creates the id column
                CATCH_COLUMN_PHOTO+ " BLOB ," + //Creates the photo column of type blob, useful for converting images to bytemaps
                CATCH_COLUMN_FISHTYPE+ " TEXT ," + //Creates the column for fishtype
                CATCH_COLUMN_FISHLENGTH+ "REAL, " +
                CATCH_COLUMN_FISHWEIGHT+ " REAL, " +
                CATCH_COLUMN_DESCRIPTION+ " TEXT, " +
                CATCH_COLUMN_LONGITUDE+ " REAL, "+
                CATCH_COLUMN_LATITUDE+ " REAL);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public CatchDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db){

            try{
                db.execSQL(SQL_CREATE_ENTRIES);
            } catch (Exception e){
                Message.message(context, "something went wrong"+e)
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"onUPgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch(Exception e){
                Message.message(context, ""+e);
            }
        }


    }


}
