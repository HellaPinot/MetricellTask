package com.hellapinot.thomassmith.metricelltask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {


    /*
    Creates database log for storing data sent through mCallback.
     */

    public static final String DATABASE_NAME = "DataLog.db";;
    public static final int DATABASE_VERSION = 2;
    private static DataBaseHelper instance = null;
    private static final String TAG = "DataBaseHelper";


    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DataBaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DataBaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sSQL = "CREATE TABLE " + DataContract.TABLE_NAME + " ( " +
                DataContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                DataContract.Columns.SIGNAL_STRENGTH + " TEXT, " +
                DataContract.Columns.SERVICE_STATE + " TEXT, " +
                DataContract.Columns.LOCATION + ");";
        db.execSQL(sSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if( newVersion != oldVersion){
            db.execSQL("DROP TABLE IF EXISTS "+ DataContract.TABLE_NAME);
            onCreate(db);
        }
    }

    public void addLogEntry(String signalStrength, String serviceState, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + DataContract.TABLE_NAME + "(" +
                DataContract.Columns.SIGNAL_STRENGTH + ", " +
                DataContract.Columns.SERVICE_STATE + ", " +
                DataContract.Columns.LOCATION + ") VALUES ('" +
                signalStrength + "', '" +
                serviceState + "', '" +
                location + "');";
        db.execSQL(query);
    }

    public int getLastRowLogData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + DataContract.TABLE_NAME, null);
        data.moveToLast();
        int id = data.getInt(0);
        data.close();
        Log.d(TAG, "getLastRowDiary: " + id);
        return id;
    }

    public Cursor getLogData(int row){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + DataContract.TABLE_NAME + " WHERE " + DataContract.Columns._ID + " = " + row + ";";
        return db.rawQuery(query, null);

    }
}

