package com.example.sebastianczuma.officevisor.Database;

/**
 * Created by sebastianczuma on 18.08.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sebastianczuma.officevisor.DataKeepers.Rooms;

import java.util.LinkedList;
import java.util.List;

public class DbHandlerRooms extends SQLiteOpenHelper {

    public DbHandlerRooms(Context context) {
        super(context, "rooms_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table pomieszczenia(" +
                        "nr integer primary key autoincrement," +
                        "nazwa text," +
                        "nazwaBudynku text," +
                        "numerPoziomu text," +
                        "typ text," +
                        "ileUrzadzen int);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Pomieszczenia
    public void addRoom(Rooms room) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("nazwa", room.getNazwa());
        wartosci.put("typ", room.getTyp());
        wartosci.put("ileUrzadzen", room.getIleUrzadzen());
        wartosci.put("nazwaBudynku", room.getNazwaBudynku());
        wartosci.put("numerPoziomu", room.getNumerPoziomu());
        db.insertOrThrow("pomieszczenia", null, wartosci);
    }

    public void deleteOneRoom(String budynek, String poziom, String nazwa) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "nazwa = ? AND nazwaBudynku = ? AND numerPoziomu = ?";

        String[] args = new String[]{
                nazwa,
                budynek,
                poziom
        };

        db.delete("pomieszczenia", whereClause, args);
    }

    public void UpdateDevicesNumber(String nazwa, int ile, String nazwaBudynku, String numerPoziomu) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues wartosci = new ContentValues();
        wartosci.put("ileUrzadzen", ile);

        String whereClause = "nazwa = ? AND nazwaBudynku = ? AND numerPoziomu = ?";
        String[] args = new String[]{
                nazwa,
                nazwaBudynku,
                numerPoziomu
        };
        db.update("pomieszczenia", wartosci, whereClause, args);
    }

    public int returnDevicesNumber(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "nazwaBudynku = ? AND numerPoziomu = ? AND nazwa = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia
        };
        String[] kolumna = {"ileUrzadzen"};
        Cursor kursor = db.query("pomieszczenia", kolumna, whereClause, args, null, null, null);
        int wynik = 0;
        while (kursor.moveToNext()) {
            wynik = kursor.getInt(0);
        }
        kursor.close();
        return wynik;
    }

    public List<Rooms> returnAllRooms(String nazwaBudynku, String numerPoziomu) {
        List<Rooms> roomses = new LinkedList<>();
        String[] kolumny = {"nr", "nazwa", "typ", "ileUrzadzen"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "nazwaBudynku = ? AND numerPoziomu = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku,
                numerPoziomu
        };

        Cursor kursor = db.query("pomieszczenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            Rooms room = new Rooms();
            room.setNr(kursor.getLong(0));
            room.setNazwa(kursor.getString(1));
            room.setTyp(kursor.getString(2));
            room.setIleUrzadzen(kursor.getInt(3));
            roomses.add(room);
        }
        kursor.close();
        return roomses;
    }

    public void deleteFloorRooms(String nazwaBudynku, String numerPietra) {
        SQLiteDatabase db = getWritableDatabase();
        String[] argumenty = {
                numerPietra,
                nazwaBudynku
        };
        db.delete("pomieszczenia", "numerPoziomu=? AND nazwaBudynku = ?", argumenty);
    }

    public int getBuildingRoomsCount(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "nazwaBudynku = ? ";
        String[] whereArgs = new String[]{
                nazwaBudynku
        };

        Cursor kursor = db.query("pomieszczenia", null, whereClause, whereArgs, null, null, null);
        //Long size = DatabaseUtils.queryNumEntries(db, "poziomy");
        //db.close();
        int size = kursor.getCount();
        kursor.close();
        return size;
    }

    public void deleteBuildingRooms(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String[] argumenty = {
                nazwaBudynku
        };
        db.delete("pomieszczenia", "nazwaBudynku = ?", argumenty);
    }
}
