package com.example.sebastianczuma.officevisor;

/**
 * Created by sebastianczuma on 09.12.2016.
 */

public class EndWordSolver {

    public static String returnEnd(int number) {
        String end;
        if (number == 1) {
            end = "nie";
        } else if (number > 10 && number < 15) {
            end = "ń";
        } else if (number % 10 > 1 && number % 10 < 5) {
            end = "nia";
        } else {
            end = "ń";
        }
        return end;
    }

    public static String returnEnd2(int number) {
        String end;
        if (number == 1) {
            end = "";
        } else if (number > 10 && number < 15) {
            end = "ów";
        } else if (number % 10 > 1 && number % 10 < 5) {
            end = "y";
        } else {
            end = "ów";
        }
        return end;
    }
}
