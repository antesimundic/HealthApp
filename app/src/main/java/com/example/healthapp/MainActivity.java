package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private TextView tvSazetakDana;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        tvSazetakDana = findViewById(R.id.tvSazetakDana);

        CardView cardDodajJelo = findViewById(R.id.cardDodajJelo);
        CardView cardPopisJela = findViewById(R.id.cardPopisJela);
        CardView cardDodajKalorije = findViewById(R.id.cardDodajKalorije);
        CardView cardDnevnik = findViewById(R.id.cardDnevnik);
        CardView cardStatistika = findViewById(R.id.cardStatistika);
        CardView cardIzbrisiJelo = findViewById(R.id.cardIzbrisiJelo);

        cardDodajJelo.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DodajJeloActivity.class)));

        cardPopisJela.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PopisJelaActivity.class)));

        cardDodajKalorije.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DodajKalorijeActivity.class)));

        cardDnevnik.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DnevnikActivity.class)));

        cardStatistika.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StatistikaActivity.class)));

        cardIzbrisiJelo.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, IzbrisiJeloActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        prikaziSazetakDana();
    }

    /** Kratki pregled danasnjeg unosa na pocetnom zaslonu. */
    private void prikaziSazetakDana() {
        String danas = dbHelper.getCurrentDate();
        int uneseno = dbHelper.ukupneKalorijeDan(danas);
        int limit = dbHelper.dohvatiDnevniLimit(danas);
        int preostalo = limit - uneseno;

        String tekst = "Danas (" + danas + ")\n"
                + "Uneseno: " + uneseno + " kcal od " + limit + " kcal";

        if (preostalo >= 0) {
            tekst += "\nPreostalo: " + preostalo + " kcal";
        } else {
            tekst += "\nPremašen limit za " + Math.abs(preostalo) + " kcal!";
        }

        tvSazetakDana.setText(tekst);
    }
}
