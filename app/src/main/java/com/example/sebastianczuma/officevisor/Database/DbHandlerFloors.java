package com.example.sebastianczuma.officevisor.Database;

/**
 * Created by sebastianczuma on 18.08.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sebastianczuma.officevisor.DataKeepers.Floors;

import java.util.LinkedList;
import java.util.List;

public class DbHandlerFloors extends SQLiteOpenHelper {

    public DbHandlerFloors(Context context) {
        super(context, "floors_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table poziomy(" +
                        "nr integer primary key autoincrement," +
                        "nazwaBudynku text," +
                        "numerPietra text," +
                        "ilePomieszczen int," +
                        "ileUrzadzen int);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Urzadzenia
    public void addFloor(Floors floor) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("nazwaBudynku", floor.getNazwaBudynku());
        wartosci.put("numerPietra", floor.getNumerPietra());
        wartosci.put("ilePomieszczen", floor.getIlePomieszczen());
        wartosci.put("ileUrzadzen", floor.getIleUrzadzen());
        db.insertOrThrow("poziomy", null, wartosci);
    }

    public String deleteOneFloor(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String numerPietra = Integer.toString(getCount(nazwaBudynku));
        String[] argumenty = {
                Integer.toString(getCount(nazwaBudynku)),
                nazwaBudynku
        };
        db.delete("poziomy", "numerPietra=? AND nazwaBudynku = ?", argumenty);
        return numerPietra;
    }

    public void UpdateRoomsNumber(String nazwaBudynku, String numerPietra, int ile) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues wartosci = new ContentValues();
        wartosci.put("ilePomieszczen", ile);

        String whereClause = "nazwaBudynku = ? AND numerPietra = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra
        };
        db.update("poziomy", wartosci, whereClause, args);
    }

    public int returnDevicesNumber(String nazwaBudynku, String numerPietra) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "nazwaBudynku = ? AND numerPietra = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra
        };
        String[] kolumna = {"ileUrzadzen"};
        Cursor kursor = db.query("poziomy", kolumna, whereClause, args, null, null, null);
        int wynik = 0;
        while (kursor.moveToNext()) {
            wynik = kursor.getInt(0);
        }
        kursor.close();
        return wynik;
    }

    public void UpdateDevicesNumber(String nazwaBudynku, String numerPietra, int ile) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues wartosci = new ContentValues();
        wartosci.put("ileUrzadzen", ile);

        String whereClause = "nazwaBudynku = ? AND numerPietra = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra
        };
        db.update("poziomy", wartosci, whereClause, args);
    }

    public List<Floors> returnAllFloors(String nazwaBudynku) {
        List<Floors> floors = new LinkedList<>();
        String[] kolumny = {"nr", "nazwaBudynku", "numerPietra", "ilePomieszczen", "ileUrzadzen"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "nazwaBudynku = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku
        };

        Cursor kursor = db.query("poziomy", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            Floors floor = new Floors();
            floor.setNr(kursor.getLong(0));
            floor.setNazwaBudynku(kursor.getString(1));
            floor.setNumerPietra(kursor.getString(2));
            floor.setIlePomieszczen(kursor.getInt(3));
            floor.setIleUrzadzen(kursor.getInt(4));
            floors.add(floor);
        }
        kursor.close();
        return floors;
    }

    private int getCount(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "nazwaBudynku = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku
        };

        Cursor kursor = db.query("poziomy", null, whereClause, whereArgs, null, null, null);
        int size = kursor.getCount();
        kursor.close();
        return size;
    }

    public void deleteBuildingFloors(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String[] argumenty = {
                nazwaBudynku
        };
        db.delete("poziomy", "nazwaBudynku = ?", argumenty);
    }
}
