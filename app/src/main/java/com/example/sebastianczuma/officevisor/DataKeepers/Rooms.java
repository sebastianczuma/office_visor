package com.example.sebastianczuma.officevisor.DataKeepers;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class Rooms {
    private Long nr;
    private String nazwa;
    private String typ;
    private String nazwaBudynku;
    private String numerPoziomu;
    private int ileUrzadzen;

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

    public int getIleUrzadzen() {
        return ileUrzadzen;
    }

    public void setIleUrzadzen(int ileUrzadzen) {
        this.ileUrzadzen = ileUrzadzen;
    }
}
