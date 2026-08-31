package com.example.healthapp;

import android.content.Context;

/**
 * Pomocna klasa koja iz naziva slike (npr. "jelo3") pronalazi sliku
 * u mapi res/drawable.
 */
public class SlikaUtil {

    public static int idSlike(Context context, String nazivSlike) {
        if (nazivSlike == null || nazivSlike.isEmpty()) {
            return R.drawable.jelo1;
        }
        int resId = context.getResources().getIdentifier(
                nazivSlike, "drawable", context.getPackageName());

        // ako slika ne postoji, prikazi prvu sliku umjesto prazne slicice
        return resId != 0 ? resId : R.drawable.jelo1;
    }
}
