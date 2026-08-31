package com.example.healthapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StatistikaActivity extends AppCompatActivity {

    private TextView tvDnevnaStatistika, tvTjednaStatistika, tvMjesecnaStatistika, tvRazdobljeStatistika;
    private Button btnOsvjezi, btnOdDatum, btnDoDatum;
    private DatabaseHelper dbHelper;

    private final Calendar odKalendar = Calendar.getInstance();
    private final Calendar doKalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistika);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        tvDnevnaStatistika = findViewById(R.id.tvDnevnaStatistika);
        tvTjednaStatistika = findViewById(R.id.tvTjednaStatistika);
        tvMjesecnaStatistika = findViewById(R.id.tvMjesecnaStatistika);
        tvRazdobljeStatistika = findViewById(R.id.tvRazdobljeStatistika);
        btnOsvjezi = findViewById(R.id.btnOsvjezi);
        btnOdDatum = findViewById(R.id.btnOdDatum);
        btnDoDatum = findViewById(R.id.btnDoDatum);

        // prema zadanome vlastito razdoblje obuhvaca zadnjih 30 dana
        odKalendar.add(Calendar.DAY_OF_MONTH, -29);

        btnOsvjezi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ucitajStatistiku();
            }
        });

        btnOdDatum.setOnClickListener(v -> odaberiDatum(odKalendar));
        btnDoDatum.setOnClickListener(v -> odaberiDatum(doKalendar));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ucitajStatistiku();
    }

    private String formatirajDatum(Calendar kalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(kalendar.getTime());
    }

    private void odaberiDatum(final Calendar kalendar) {
        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        kalendar.set(year, month, dayOfMonth);
                        ucitajStatistiku();
                    }
                },
                kalendar.get(Calendar.YEAR),
                kalendar.get(Calendar.MONTH),
                kalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void ucitajStatistiku() {
        prikaziDanas();
        prikaziTjedan();
        prikaziMjesec();
        prikaziVlastitoRazdoblje();
    }

    private void prikaziDanas() {
        String danas = dbHelper.getCurrentDate();
        int kalorije = dbHelper.ukupneKalorijeDan(danas);
        int limit = dbHelper.dohvatiDnevniLimit(danas);

        String tekst = "DANAS (" + danas + ")\n"
                + "Uneseno: " + kalorije + " kcal\n"
                + "Limit: " + limit + " kcal\n"
                + "Preostalo: " + (limit - kalorije) + " kcal";

        if (kalorije > limit) {
            tekst += "\nPremašen dnevni limit!";
        }

        tvDnevnaStatistika.setText(tekst);
    }

    private void prikaziTjedan() {
        Calendar cal = Calendar.getInstance();

        // pomak unatrag do prvog dana tekuceg tjedna
        int pomak = (cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek() + 7) % 7;
        cal.add(Calendar.DAY_OF_MONTH, -pomak);
        String pocetak = formatirajDatum(cal);

        cal.add(Calendar.DAY_OF_MONTH, 6);
        String kraj = formatirajDatum(cal);

        int ukupno = dbHelper.ukupneKalorijeRazdoblje(pocetak, kraj);

        String tekst = "OVAJ TJEDAN (" + pocetak + " - " + kraj + ")\n"
                + "Ukupno uneseno: " + ukupno + " kcal\n"
                + "Prosjek po danu: " + (ukupno / 7) + " kcal";

        tvTjednaStatistika.setText(tekst);
    }

    private void prikaziMjesec() {
        Calendar cal = Calendar.getInstance();
        int brojDana = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        String pocetak = formatirajDatum(cal);

        cal.set(Calendar.DAY_OF_MONTH, brojDana);
        String kraj = formatirajDatum(cal);

        int ukupno = dbHelper.ukupneKalorijeRazdoblje(pocetak, kraj);

        String tekst = "OVAJ MJESEC (" + pocetak + " - " + kraj + ")\n"
                + "Ukupno uneseno: " + ukupno + " kcal\n"
                + "Prosjek po danu: " + (ukupno / brojDana) + " kcal";

        tvMjesecnaStatistika.setText(tekst);
    }

    private void prikaziVlastitoRazdoblje() {
        String od = formatirajDatum(odKalendar);
        String doDatuma = formatirajDatum(doKalendar);

        btnOdDatum.setText("Od: " + od);
        btnDoDatum.setText("Do: " + doDatuma);

        if (od.compareTo(doDatuma) > 0) {
            tvRazdobljeStatistika.setText("Početni datum je nakon završnog datuma.");
            return;
        }

        int ukupno = dbHelper.ukupneKalorijeRazdoblje(od, doDatuma);
        int brojDana = brojDanaURazdoblju();
        int danaSUnosom = dbHelper.brojDanaSUnosom(od, doDatuma);

        String tekst = "RAZDOBLJE (" + od + " - " + doDatuma + ")\n"
                + "Broj dana: " + brojDana + "\n"
                + "Ukupno uneseno: " + ukupno + " kcal\n"
                + "Prosjek po danu: " + (ukupno / brojDana) + " kcal\n"
                + "Dana s unosom: " + danaSUnosom;

        tvRazdobljeStatistika.setText(tekst);
    }

    /** Broj dana izmedu odabranih datuma, ukljucujuci oba datuma. */
    private int brojDanaURazdoblju() {
        long milisekundiPoDanu = 24L * 60 * 60 * 1000;
        long razlika = doKalendar.getTimeInMillis() - odKalendar.getTimeInMillis();
        return (int) Math.round((double) razlika / milisekundiPoDanu) + 1;
    }
}
