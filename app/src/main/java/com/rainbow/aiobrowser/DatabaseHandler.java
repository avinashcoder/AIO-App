package com.rainbow.aiobrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "allinone";
    private static final String TABLE_NAME = "appdata";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String TARGET_URL = "target_url";
    private static final String APP_TYPE = "app_type";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IMAGE_URL + " TEXT,"+ TARGET_URL + " TEXT,"+ APP_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addFavourite(AppsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_ID,model.getId() );
        values.put(KEY_NAME, model.getName());
        values.put(KEY_IMAGE_URL, model.getImageUrl());
        values.put(TARGET_URL, model.getTargetUrl());
        values.put( APP_TYPE,model.getAppType() );

        // Inserting Row
        db.insert( TABLE_NAME, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all contacts in a list view
    public ArrayList<AppsModel> getFavourite() {
        ArrayList<AppsModel> appsModelList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppsModel app = new AppsModel();
                app.setId(Integer.parseInt(cursor.getString(0)));
                app.setName(cursor.getString(1));
                app.setImageUrl(cursor.getString(2));
                app.setTargetUrl(cursor.getString(3));
                app.setAppType(cursor.getString(4));
                // Adding contact to list
                appsModelList.add(app);
            } while (cursor.moveToNext());
        }

        // return contact list
        return appsModelList;
    }

    public Boolean checkList(int id){
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_ID +" = "+id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return  true;
        }else{
            return false;
        }
    }
/*
    // code to update the single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update( TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

 */

    // Deleting single contact
    public void removeFavourite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
