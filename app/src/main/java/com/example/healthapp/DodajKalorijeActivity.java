package com.example.healthapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DodajKalorijeActivity extends AppCompatActivity {

    private Spinner spJelo;
    private EditText etKolicina, etDnevniLimit;
    private TextView tvOdabraniDatum, tvOdabranoVrijeme, tvTrenutneKalorije;
    private Button btnOdaberiDatum, btnOdaberiVrijeme, btnDodajKalorije, btnPostaviLimit;
    private DatabaseHelper dbHelper;
    private List<Jelo> jela;

    private final Calendar odabraniKalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kalorije);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        spJelo = findViewById(R.id.spJelo);
        etKolicina = findViewById(R.id.etKolicina);
        etDnevniLimit = findViewById(R.id.etDnevniLimit);
        tvOdabraniDatum = findViewById(R.id.tvOdabraniDatum);
        tvOdabranoVrijeme = findViewById(R.id.tvOdabranoVrijeme);
        tvTrenutneKalorije = findViewById(R.id.tvTrenutneKalorije);
        btnOdaberiDatum = findViewById(R.id.btnOdaberiDatum);
        btnOdaberiVrijeme = findViewById(R.id.btnOdaberiVrijeme);
        btnDodajKalorije = findViewById(R.id.btnDodajKalorije);
        btnPostaviLimit = findViewById(R.id.btnPostaviLimit);

        prikaziDatumIVrijeme();

        btnOdaberiDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziOdabirDatuma();
            }
        });

        btnOdaberiVrijeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziOdabirVremena();
            }
        });

        btnDodajKalorije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajUnos();
            }
        });

        btnPostaviLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postaviLimit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // popis jela se osvjezava jer je korisnik mozda dodao novo jelo
        ucitajJela();
        osvjeziKalorije();
    }

    private void ucitajJela() {
        jela = dbHelper.dohvatiSvaJela();
        ArrayAdapter<Jelo> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, jela);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJelo.setAdapter(adapter);
    }

    // ---------- datum i vrijeme ----------

    private String getOdabraniDatum() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(odabraniKalendar.getTime());
    }

    private String getOdabranoVrijeme() {
        return String.format(Locale.getDefault(), "%02d:%02d",
                odabraniKalendar.get(Calendar.HOUR_OF_DAY),
                odabraniKalendar.get(Calendar.MINUTE));
    }

    private void prikaziDatumIVrijeme() {
        tvOdabraniDatum.setText(getOdabraniDatum());
        tvOdabranoVrijeme.setText(getOdabranoVrijeme());
    }

    private void prikaziOdabirDatuma() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        odabraniKalendar.set(Calendar.YEAR, year);
                        odabraniKalendar.set(Calendar.MONTH, month);
                        odabraniKalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        prikaziDatumIVrijeme();
                        osvjeziKalorije();
                    }
                },
                odabraniKalendar.get(Calendar.YEAR),
                odabraniKalendar.get(Calendar.MONTH),
                odabraniKalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void prikaziOdabirVremena() {
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        odabraniKalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        odabraniKalendar.set(Calendar.MINUTE, minute);
                        prikaziDatumIVrijeme();
                    }
                },
                odabraniKalendar.get(Calendar.HOUR_OF_DAY),
                odabraniKalendar.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }

    // ---------- spremanje ----------

    private void dodajUnos() {
        if (spJelo.getSelectedItem() == null) {
            Toast.makeText(this, "Najprije dodajte jelo u popis", Toast.LENGTH_SHORT).show();
            return;
        }

        String kolicinaStr = etKolicina.getText().toString().trim();
        if (kolicinaStr.isEmpty()) {
            kolicinaStr = "1";
        }

        try {
            int kolicina = Integer.parseInt(kolicinaStr);
            if (kolicina <= 0) {
                Toast.makeText(this, "Količina mora biti pozitivna", Toast.LENGTH_SHORT).show();
                return;
            }

            Jelo odabranoJelo = (Jelo) spJelo.getSelectedItem();
            boolean uspjeh = dbHelper.dodajPotrosnju(odabranoJelo.getId(),
                    getOdabraniDatum(), getOdabranoVrijeme(), kolicina);

            if (uspjeh) {
                Toast.makeText(this, "Obrok spremljen u dnevnik", Toast.LENGTH_SHORT).show();
                etKolicina.setText("");
                osvjeziKalorije();
            } else {
                Toast.makeText(this, "Greška pri spremanju obroka", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Unesite valjanu količinu", Toast.LENGTH_SHORT).show();
        }
    }

    private void postaviLimit() {
        String limitStr = etDnevniLimit.getText().toString().trim();
        if (limitStr.isEmpty()) {
            Toast.makeText(this, "Molimo unesite dnevni limit", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int limit = Integer.parseInt(limitStr);
            if (limit <= 0) {
                Toast.makeText(this, "Limit mora biti pozitivan", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean uspjeh = dbHelper.postaviDnevniLimit(getOdabraniDatum(), limit);
            if (uspjeh) {
                Toast.makeText(this, "Dnevni limit postavljen", Toast.LENGTH_SHORT).show();
                etDnevniLimit.setText("");
                osvjeziKalorije();
            } else {
                Toast.makeText(this, "Greška pri postavljanju limita", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Unesite valjani broj za limit", Toast.LENGTH_SHORT).show();
        }
    }

    private void osvjeziKalorije() {
        String datum = getOdabraniDatum();
        int ukupneKalorije = dbHelper.ukupneKalorijeDan(datum);
        int dnevniLimit = dbHelper.dohvatiDnevniLimit(datum);
        int preostalo = dnevniLimit - ukupneKalorije;

        String tekst = "Datum: " + datum + "\n"
                + "Uneseno: " + ukupneKalorije + " kcal\n"
                + "Limit: " + dnevniLimit + " kcal\n"
                + "Preostalo: " + preostalo + " kcal";

        if (preostalo < 0) {
            tekst += "\nPremašen limit za " + Math.abs(preostalo) + " kcal!";
        }

        tvTrenutneKalorije.setText(tekst);
    }
}
