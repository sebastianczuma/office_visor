package com.example.sebastianczuma.officevisor.DataKeepers;

/**
 * Created by sebastianczuma on 08.12.2016.
 */

public class Buildings {
    Long nr;
    private String nazwa;
    private int ilePieter;

    public int getIlePieter() {
        return ilePieter;
    }

    public void setIlePieter(int ilePieter) {
        this.ilePieter = ilePieter;
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

}
