package com.example.vikramkumaresan.v1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DB_Handler extends SQLiteOpenHelper {

    private static final int Database_Version=1;
    private static final String Database_Name="OfflineData.db";
    private static final String Table_Current_Weather="Current_Weather";
    private static final String Column_City="City";
    private static final String Column_Temp_Max = "Temp_Max";
    private static final String Column_Temp_Min= "Temp_Min";
    private static final String Column_Sky="Sky";

    private static final String Table_Forecast_Weather="Forecast_Weather";
    private static final String Column_Date="Date";
    //private static final String Column_City="City";
    //private static final String Column_Temp_Max = "Temp_Max";
    //private static final String Column_Temp_Min= "Temp_Min";
    //private static final String Column_Sky="Sky";


    public DB_Handler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,context.getExternalFilesDir("Databases").getPath()+"/"+Database_Name, factory, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE "+Table_Current_Weather+"(" +
                Column_City +" VARCHAR(50)," +
                Column_Temp_Max+" VARCHAR(50),"+
                Column_Temp_Min+" VARCHAR(50),"+
                Column_Sky+" VARCHAR(50));";

        String query2="CREATE TABLE "+Table_Forecast_Weather+"("+
                Column_City +" VARCHAR(50)," +
                Column_Date +" VARCHAR(180)," +
                Column_Temp_Max+" VARCHAR(180),"+
                Column_Temp_Min+" VARCHAR(180),"+
                Column_Sky+" VARCHAR(180));";

        db.execSQL(query1);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertCurrent(String city,String temp_max,String temp_min, String sky){
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO "+Table_Current_Weather+" VALUES("+"'"+city+"',"+"'"+temp_max+"','"+temp_min+"','"+sky+"');";
        db.execSQL(query);
    }

    public void insertForecast(String city,String date,String temp_max,String temp_min, String sky){
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO "+Table_Forecast_Weather+" VALUES('"+city+"','"+date+"','"+temp_max+"','"+temp_min+"','"+sky+"');";
        db.execSQL(query);
    }

    public boolean isCityExist(String city,String table){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT 'City' FROM "+ table +" WHERE City='"+city+"';";

        Cursor cursor =db.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount()==1){
            return true;
        }
        else {
            return false;
        }
    }

    public void delCity(String city,String table){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM '"+table+"' WHERE City='"+city+"';";
        db.execSQL(query);
    }

    public ArrayList<String> getCurrentWeather(String city){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> data = new ArrayList<>();

        String query = "SELECT * FROM "+ Table_Current_Weather +" WHERE City='"+city+"';";

        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        data.add(cursor.getString(cursor.getColumnIndex(Column_City)));
        data.add(cursor.getString(cursor.getColumnIndex(Column_Temp_Max)));
        data.add(cursor.getString(cursor.getColumnIndex(Column_Temp_Min)));
        data.add(cursor.getString(cursor.getColumnIndex(Column_Sky)));

        return data;
    }
    public void getForecastWeather(String city){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<ArrayList<String>> data = new ArrayList<>();

        String query = "SELECT * FROM "+ Table_Forecast_Weather +" WHERE City='"+city+"';";

        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        String[] date_list = cursor.getString(cursor.getColumnIndex(Column_Date)).split("\\+");
        String[] temp_max_list = cursor.getString(cursor.getColumnIndex(Column_Temp_Max)).split("\\+");
        String[] temp_min_list = cursor.getString(cursor.getColumnIndex(Column_Temp_Min)).split("\\+");
        String[] sky_list = cursor.getString(cursor.getColumnIndex(Column_Sky)).split("\\+");

        for(int i=0;i<5;i++){
            ArrayList<String> stuff = new ArrayList<>();
            stuff.add(date_list[i]);
            stuff.add(temp_max_list[i]);
            stuff.add(temp_min_list[i]);
            stuff.add(sky_list[i]);
            data.add(stuff);
        }

        MainActivity.fiveday_forecast_update(city,data);

    }
}
