package com.example.sebastianczuma.officevisor.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sebastianczuma.officevisor.DataKeepers.Buildings;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sebastianczuma on 08.12.2016.
 */

public class DbHandlerBuildings extends SQLiteOpenHelper {

    public DbHandlerBuildings(Context context) {
        super(context, "buildings_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table budynki(" +
                        "nr integer primary key autoincrement," +
                        "nazwa text," +
                        "ilePieter int);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Budynki
    public void addBuilding(Buildings building) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("nazwa", building.getNazwa());

        db.insertOrThrow("budynki", null, wartosci);
    }

    public void deleteOneBuilding(String nazwa) {
        SQLiteDatabase db = getWritableDatabase();
        String[] argumenty = {"" + nazwa};
        db.delete("budynki", "nazwa = ?", argumenty);
    }


    public List<Buildings> returnAllBuildings() {
        List<Buildings> buildings = new LinkedList<>();
        String[] kolumny = {"nr", "nazwa", "ilePieter"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor kursor = db.query("budynki", kolumny, null, null, null, null, null);
        while (kursor.moveToNext()) {
            Buildings building = new Buildings();
            building.setNr(kursor.getLong(0));
            building.setNazwa(kursor.getString(1));
            buildings.add(building);
        }
        kursor.close();
        return buildings;
    }
}
