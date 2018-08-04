package com.example.sebastianczuma.officevisor.Database;

/**
 * Created by sebastianczuma on 18.08.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sebastianczuma.officevisor.DataKeepers.Devices;

import java.util.LinkedList;
import java.util.List;

public class DbHandlerDevices extends SQLiteOpenHelper {

    public DbHandlerDevices(Context context) {
        super(context, "devices_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table urzadzenia(" +
                        "ID_URZADZENIA integer primary key autoincrement," +
                        "NAZWA_URZADZENIA text," +
                        "NAZWA_BUDYNKU text," +
                        "NUMER_POZIOMU text," +
                        "NAZWA_POMIESZCZENIA text," +
                        "ULUBIONE text," +
                        "POZYCJA text," +
                        "ALARM text," +
                        "ALARM_WLACZONY text," +
                        "MIN integer," +
                        "MAX integer," +
                        "TYP_URZADZENIA text);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Urzadzenia
    public void addDevice(Devices device) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("NAZWA_URZADZENIA", device.getNazwa());
        wartosci.put("TYP_URZADZENIA", device.getTyp());
        wartosci.put("NAZWA_BUDYNKU", device.getNazwaBudynku());
        wartosci.put("NUMER_POZIOMU", device.getNumerPoziomu());
        wartosci.put("NAZWA_POMIESZCZENIA", device.getPomieszczenie());
        wartosci.put("ULUBIONE", device.getUlubione());
        wartosci.put("POZYCJA", device.getPozycja());

        db.insertOrThrow("urzadzenia", null, wartosci);
    }

    public void deleteOneDevice(String nazwaBudynku,
                                String numerPietra,
                                String nazwaPomieszczenia,
                                String nazwaUrzadzenia) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? " +
                "AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";

        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };

        db.delete("urzadzenia", whereClause, args);
    }

    public List<Devices> returnAllDevices(String pomieszczenie, String poziom, String budynek) {
        List<Devices> devices = new LinkedList<>();
        String[] kolumny = {"ID_URZADZENIA", "NAZWA_URZADZENIA", "NAZWA_BUDYNKU", "NUMER_POZIOMU", "NAZWA_POMIESZCZENIA", "ULUBIONE", "TYP_URZADZENIA", "POZYCJA", "ALARM_WLACZONY"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "NAZWA_POMIESZCZENIA = ? AND NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ?";
        String[] whereArgs = new String[]{
                pomieszczenie,
                budynek,
                poziom
        };

        Cursor kursor = db.query("urzadzenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            Devices device = new Devices();
            device.setNr(kursor.getLong(0));
            device.setNazwa(kursor.getString(1));
            device.setNazwaBudynku(kursor.getString(2));
            device.setNumerPoziomu(kursor.getString(3));
            device.setPomieszczenie(kursor.getString(4));
            device.setUlubione(kursor.getString(5));
            device.setTyp(kursor.getString(6));
            device.setPozycja(kursor.getString(7));
            device.setIsAlarmActive(kursor.getString(8));
            devices.add(device);
        }
        kursor.close();
        return devices;
    }

    public List<Devices> returnAllDevicesForBackgroundService() {
        List<Devices> devices = new LinkedList<>();
        String[] kolumny = {"NAZWA_BUDYNKU", "NUMER_POZIOMU", "NAZWA_POMIESZCZENIA", "NAZWA_URZADZENIA", "TYP_URZADZENIA"};
        SQLiteDatabase db = getReadableDatabase();

        Cursor kursor = db.query("urzadzenia", kolumny, null, null, null, null, null);
        while (kursor.moveToNext()) {
            Devices device = new Devices();
            device.setNazwaBudynku(kursor.getString(0));
            device.setNumerPoziomu(kursor.getString(1));
            device.setPomieszczenie(kursor.getString(2));
            device.setNazwa(kursor.getString(3));
            device.setTyp(kursor.getString(4));
            devices.add(device);
        }
        kursor.close();
        return devices;
    }

    public void deleteFloorDevices(String nazwaBudynku, String numerPietra) {
        SQLiteDatabase db = getWritableDatabase();
        String[] argumenty = {
                numerPietra,
                nazwaBudynku
        };
        db.delete("urzadzenia", "NUMER_POZIOMU=? AND NAZWA_BUDYNKU = ?", argumenty);
    }

    public int getFloorDevicesCount(String nazwaBudynku, String numerPietra) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku,
                numerPietra
        };

        Cursor kursor = db.query("urzadzenia", null, whereClause, whereArgs, null, null, null);
        //Long size = DatabaseUtils.queryNumEntries(db, "poziomy");
        //db.close();
        int size = kursor.getCount();
        kursor.close();
        return size;
    }

    public int getBuildingDevicesCount(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "NAZWA_BUDYNKU = ? ";
        String[] whereArgs = new String[]{
                nazwaBudynku
        };

        Cursor kursor = db.query("urzadzenia", null, whereClause, whereArgs, null, null, null);
        //Long size = DatabaseUtils.queryNumEntries(db, "poziomy");
        //db.close();
        int size = kursor.getCount();
        kursor.close();
        return size;
    }

    public void deleteBuildingDevices(String nazwaBudynku) {
        SQLiteDatabase db = getWritableDatabase();
        String[] argumenty = {
                nazwaBudynku
        };
        db.delete("urzadzenia", "NAZWA_BUDYNKU = ?", argumenty);
    }

    public List<Devices> returnAllFavouritesDevices() {
        List<Devices> devices = new LinkedList<>();

        String[] kolumny = {"ID_URZADZENIA", "NAZWA_URZADZENIA", "NAZWA_BUDYNKU", "NUMER_POZIOMU", "NAZWA_POMIESZCZENIA", "ULUBIONE", "TYP_URZADZENIA", "POZYCJA", "ALARM_WLACZONY"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "ULUBIONE = ?";
        String[] whereArgs = new String[]{
                "tak"
        };

        Cursor kursor = db.query("urzadzenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            Devices device = new Devices();
            device.setNr(kursor.getLong(0));
            device.setNazwa(kursor.getString(1));
            device.setNazwaBudynku(kursor.getString(2));
            device.setNumerPoziomu(kursor.getString(3));
            device.setPomieszczenie(kursor.getString(4));
            device.setUlubione(kursor.getString(5));
            device.setTyp(kursor.getString(6));
            device.setPozycja(kursor.getString(7));
            device.setIsAlarmActive(kursor.getString(8));
            devices.add(device);
        }
        kursor.close();
        return devices;
    }

    public void addOrRemoveNewFavourite(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia, String what) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("ULUBIONE", what);

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };
        db.update("urzadzenia", wartosci, whereClause, args);
    }

    public void deleteRoomDevices(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ?";

        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia
        };

        db.delete("urzadzenia", whereClause, args);
    }

    public void updateDeviceAlarm(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia, String alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("ALARM", alarm);

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };
        db.update("urzadzenia", wartosci, whereClause, args);
    }

    public void updateDeviceAlarmMinValue(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia, int min) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("MIN", min);

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };
        db.update("urzadzenia", wartosci, whereClause, args);
    }

    public void updateDeviceAlarmMaxValue(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia, int max) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("MAX", max);

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };
        db.update("urzadzenia", wartosci, whereClause, args);
    }

    public String returnDeviceAlarm(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia) {
        String data = "";

        String[] kolumny = {"ALARM"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };

        Cursor kursor = db.query("urzadzenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            data = kursor.getString(0);
        }
        kursor.close();
        return data;
    }

    public int returnDeviceAlarmMinValue(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia) {
        int data = 0;

        String[] kolumny = {"MIN"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };

        Cursor kursor = db.query("urzadzenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            data = kursor.getInt(0);
        }
        kursor.close();
        return data;
    }

    public int returnDeviceAlarmMaxValue(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia) {
        int data = 0;

        String[] kolumny = {"MAX"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };

        Cursor kursor = db.query("urzadzenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            data = kursor.getInt(0);
        }
        kursor.close();
        return data;
    }

    public void updateIsDeviceAlarmActive(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia, String alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("ALARM_WLACZONY", alarm);

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] args = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };
        db.update("urzadzenia", wartosci, whereClause, args);
    }

    public String returnIsDeviceAlarmActive(String nazwaBudynku, String numerPietra, String nazwaPomieszczenia, String nazwaUrzadzenia) {
        String data = "";

        String[] kolumny = {"ALARM_WLACZONY"};
        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "NAZWA_BUDYNKU = ? AND NUMER_POZIOMU = ? AND NAZWA_POMIESZCZENIA = ? AND NAZWA_URZADZENIA = ?";
        String[] whereArgs = new String[]{
                nazwaBudynku,
                numerPietra,
                nazwaPomieszczenia,
                nazwaUrzadzenia
        };

        Cursor kursor = db.query("urzadzenia", kolumny, whereClause, whereArgs, null, null, null);
        while (kursor.moveToNext()) {
            data = kursor.getString(0);
        }
        kursor.close();
        return data;
    }
}
