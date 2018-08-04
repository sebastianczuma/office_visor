package com.example.sebastianczuma.officevisor.LayoutClassesDevices;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class ItemObjectDevice {

    private String name;
    private String type;
    private String nazwaBudynku;
    private String numerPoziomu;
    private String nazwaPomieszczenia;
    private String ulubione;
    private String pozycja;
    private int photo;
    private String data;
    private String isAlarmActive;

    public ItemObjectDevice(String name, String type, String nazwaBudynku, String numerPoziomu, String nazwaPomieszczenia, String ulubione, int photo, String data, String pozycja, String isAlarmActive) {
        this.name = name;
        this.photo = photo;
        this.type = type;
        this.data = data;
        this.nazwaBudynku = nazwaBudynku;
        this.numerPoziomu = numerPoziomu;
        this.nazwaPomieszczenia = nazwaPomieszczenia;
        this.ulubione = ulubione;
        this.pozycja = pozycja;
        this.isAlarmActive = isAlarmActive;
    }

    public String getIsAlarmActive() {
        return isAlarmActive;
    }

    public void setIsAlarmActive(String isAlarmActive) {
        this.isAlarmActive = isAlarmActive;
    }

    public String getUlubione() {
        return ulubione;
    }

    public void setUlubione(String ulubione) {
        this.ulubione = ulubione;
    }

    public String getNazwaPomieszczenia() {
        return nazwaPomieszczenia;
    }

    public void setNazwaPomieszczenia(String nazwaPomieszczenia) {
        this.nazwaPomieszczenia = nazwaPomieszczenia;
    }

    public String getNazwaBudynku() {
        return nazwaBudynku;
    }

    public void setNazwaBudynku(String nazwaBudynku) {
        this.nazwaBudynku = nazwaBudynku;
    }

    public String getNumerPoziomu() {
        return numerPoziomu;
    }

    public void setNumerPoziomu(String numerPoziomu) {
        this.numerPoziomu = numerPoziomu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPozycja() {
        return pozycja;
    }

    public void setPozycja(String pozycja) {
        this.pozycja = pozycja;
    }
}