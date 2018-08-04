package com.example.sebastianczuma.officevisor.DataKeepers;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class Devices {
    private Long nr;
    private String pomieszczenie;
    private String nazwa;
    private String typ;
    private String nazwaBudynku;
    private String numerPoziomu;
    private String ulubione;
    private String pozycja;
    private String isAlarmActive;

    public String getIsAlarmActive() {
        return isAlarmActive;
    }

    public void setIsAlarmActive(String isAlarmActive) {
        this.isAlarmActive = isAlarmActive;
    }

    public String getPozycja() {
        return pozycja;
    }

    public void setPozycja(String pozycja) {
        this.pozycja = pozycja;
    }

    public String getUlubione() {
        return ulubione;
    }

    public void setUlubione(String ulubione) {
        this.ulubione = ulubione;
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

    public String getPomieszczenie() {
        return pomieszczenie;
    }

    public void setPomieszczenie(String pomieszczenie) {
        this.pomieszczenie = pomieszczenie;
    }

    public Long getNr() {
        return nr;
    }

    public void setNr(Long nr) {
        this.nr = nr;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

}
