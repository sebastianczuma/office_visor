package com.example.sebastianczuma.officevisor.DataKeepers;

/**
 * Created by sebastianczuma on 08.12.2016.
 */

public class Floors {
    Long nr;
    private String nazwaBudynku;
    private String numerPietra;
    private int ilePomieszczen;
    private int ileUrzadzen;

    public Long getNr() {
        return nr;
    }

    public void setNr(Long nr) {
        this.nr = nr;
    }

    public String getNazwaBudynku() {
        return nazwaBudynku;
    }

    public void setNazwaBudynku(String nazwaBudynku) {
        this.nazwaBudynku = nazwaBudynku;
    }

    public String getNumerPietra() {
        return numerPietra;
    }

    public void setNumerPietra(String numerPietra) {
        this.numerPietra = numerPietra;
    }

    public int getIlePomieszczen() {
        return ilePomieszczen;
    }

    public void setIlePomieszczen(int ilePomieszczen) {
        this.ilePomieszczen = ilePomieszczen;
    }

    public int getIleUrzadzen() {
        return ileUrzadzen;
    }

    public void setIleUrzadzen(int ileUrzadzen) {
        this.ileUrzadzen = ileUrzadzen;
    }
}
