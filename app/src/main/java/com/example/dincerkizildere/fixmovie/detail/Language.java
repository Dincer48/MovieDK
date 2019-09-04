package com.example.dincerkizildere.fixmovie.detail;

import java.util.Locale;

public class Language {

    public static String getCountry() {
        String country = Locale.getDefault().getCountry().toLowerCase();

        switch (country) {
            case "id":
                break;

            default:
                country = "tr";
                break;
        }

        return country;
    }
}