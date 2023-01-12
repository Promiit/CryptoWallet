package com.promit.cryptowallet;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {


    private static final String DB_NAME = "addressdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "addresses";
    private static final String ID_COL = "id";
    private static final String ADDR_COL = "name";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ADDR_COL + " TEXT NOT NULL UNIQUE)";

        db.execSQL(query);
    }

    /*
    Return ALL addresses in db in a List format, where each String is an address
     */
    public List<String> getAddresses() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT " + ADDR_COL + " FROM " + TABLE_NAME, null);
        List<String> returnAddresses = new ArrayList<>();
        if (!c.moveToFirst()) return returnAddresses;
        do {
            String addr = c.getString(0);
            returnAddresses.add(addr);
        } while (c.moveToNext());

        c.close();
        db.close();
        return returnAddresses;
    }

    /*
    Remove address @param from db
     */
    public void removeAddress(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ADDR_COL + "='" + address +"'", null);
    }


    public void addAddress(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ADDR_COL, address);
        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

