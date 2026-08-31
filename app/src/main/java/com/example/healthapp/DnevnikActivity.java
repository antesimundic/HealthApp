package com.example.healthapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Dnevnik svih zabiljezenih obroka u odabranom razdoblju.
 * Klikom na unos otvara se izbornik za uredivanje ili brisanje.
 */
public class DnevnikActivity extends AppCompatActivity {

    private ListView lvUnosi;
    private TextView tvZbroj, tvPrazno;
    private Button btnOdDatum, btnDoDatum;
    private DatabaseHelper dbHelper;
    private List<Unos> unosi;

    private final Calendar odKalendar = Calendar.getInstance();
    private final Calendar doKalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnevnik);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        lvUnosi = findViewById(R.id.lvUnosi);
        tvZbroj = findViewById(R.id.tvZbrojRazdoblja);
        tvPrazno = findViewById(R.id.tvPraznoDnevnik);
        btnOdDatum = findViewById(R.id.btnOdDatum);
        btnDoDatum = findViewById(R.id.btnDoDatum);

        // prema zadanome se prikazuje zadnjih 7 dana
        odKalendar.add(Calendar.DAY_OF_MONTH, -6);

        btnOdDatum.setOnClickListener(v -> odaberiDatum(odKalendar));
        btnDoDatum.setOnClickListener(v -> odaberiDatum(doKalendar));

        lvUnosi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prikaziIzbornikUnosa(unosi.get(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ucitajUnose();
    }

    private String formatirajDatum(Calendar kalendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(kalendar.getTime());
    }

    private void odaberiDatum(final Calendar kalendar) {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        kalendar.set(year, month, dayOfMonth);
                        ucitajUnose();
                    }
                },
                kalendar.get(Calendar.YEAR),
                kalendar.get(Calendar.MONTH),
                kalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void ucitajUnose() {
        String od = formatirajDatum(odKalendar);
        String doDatuma = formatirajDatum(doKalendar);

        btnOdDatum.setText("Od: " + od);
        btnDoDatum.setText("Do: " + doDatuma);

        // ako je pocetni datum nakon zavrsnog, razdoblje je prazno
        if (od.compareTo(doDatuma) > 0) {
            unosi = new ArrayList<>();
            tvZbroj.setText("Početni datum je nakon završnog.");
        } else {
            unosi = dbHelper.dohvatiUnose(od, doDatuma);
            int ukupno = dbHelper.ukupneKalorijeRazdoblje(od, doDatuma);
            tvZbroj.setText("Ukupno: " + ukupno + " kcal  (" + unosi.size() + " unosa)");
        }

        lvUnosi.setAdapter(new UnosAdapter(this, unosi));
        tvPrazno.setVisibility(unosi.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void prikaziIzbornikUnosa(final Unos unos) {
        String[] opcije = {"Uredi unos", "Izbriši unos"};

        new AlertDialog.Builder(this)
                .setTitle(unos.getJeloNaziv())
                .setItems(opcije, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            otvoriUredivanje(unos);
                        } else {
                            potvrdiBrisanje(unos);
                        }
                    }
                })
                .setNegativeButton("Odustani", null)
                .show();
    }

    private void otvoriUredivanje(Unos unos) {
        Intent intent = new Intent(this, UrediUnosActivity.class);
        intent.putExtra("unos_id", unos.getId());
        intent.putExtra("jelo_id", unos.getJeloId());
        intent.putExtra("datum", unos.getDatum());
        intent.putExtra("vrijeme", unos.getVrijeme());
        intent.putExtra("kolicina", unos.getKolicina());
        startActivity(intent);
    }

    private void potvrdiBrisanje(final Unos unos) {
        new AlertDialog.Builder(this)
                .setTitle("Potvrdi brisanje")
                .setMessage("Izbrisati unos \"" + unos.getJeloNaziv() + "\" od "
                        + unos.getDatum() + " u " + unos.getVrijeme() + "?")
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.izbrisiPotrosnju(unos.getId());
                        ucitajUnose();
                    }
                })
                .setNegativeButton("Ne", null)
                .show();
    }
}
