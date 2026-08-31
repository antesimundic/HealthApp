package com.example.healthapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

/** Uredivanje ili brisanje jednog unosa iz dnevnika. */
public class UrediUnosActivity extends AppCompatActivity {

    private Spinner spJelo;
    private EditText etKolicina;
    private TextView tvDatum, tvVrijeme;
    private Button btnDatum, btnVrijeme, btnSpremi, btnIzbrisi;
    private DatabaseHelper dbHelper;
    private List<Jelo> jela;

    private int unosId;
    private final Calendar kalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredi_unos);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        spJelo = findViewById(R.id.spJeloUredi);
        etKolicina = findViewById(R.id.etKolicinaUredi);
        tvDatum = findViewById(R.id.tvDatumUredi);
        tvVrijeme = findViewById(R.id.tvVrijemeUredi);
        btnDatum = findViewById(R.id.btnDatumUredi);
        btnVrijeme = findViewById(R.id.btnVrijemeUredi);
        btnSpremi = findViewById(R.id.btnSpremiUnos);
        btnIzbrisi = findViewById(R.id.btnIzbrisiUnos);

        unosId = getIntent().getIntExtra("unos_id", -1);
        int jeloId = getIntent().getIntExtra("jelo_id", -1);
        String datum = getIntent().getStringExtra("datum");
        String vrijeme = getIntent().getStringExtra("vrijeme");
        int kolicina = getIntent().getIntExtra("kolicina", 1);

        if (unosId == -1) {
            Toast.makeText(this, "Unos nije pronađen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ucitajJela(jeloId);
        postaviDatumIVrijeme(datum, vrijeme);
        etKolicina.setText(String.valueOf(kolicina));

        btnDatum.setOnClickListener(v -> odaberiDatum());
        btnVrijeme.setOnClickListener(v -> odaberiVrijeme());
        btnSpremi.setOnClickListener(v -> spremi());
        btnIzbrisi.setOnClickListener(v -> potvrdiBrisanje());
    }

    private void ucitajJela(int jeloId) {
        jela = dbHelper.dohvatiSvaJela();
        ArrayAdapter<Jelo> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, jela);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJelo.setAdapter(adapter);

        // odabir jela koje je vec bilo spremljeno u ovom unosu
        for (int i = 0; i < jela.size(); i++) {
            if (jela.get(i).getId() == jeloId) {
                spJelo.setSelection(i);
                break;
            }
        }
    }

    /** Postavlja kalendar iz spremljenog datuma i vremena unosa. */
    private void postaviDatumIVrijeme(String datum, String vrijeme) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            kalendar.setTime(sdf.parse(datum + " " + vrijeme));
        } catch (Exception e) {
            // ako podaci nisu ispravni, koristi se trenutni datum i vrijeme
        }
        prikaziDatumIVrijeme();
    }

    private String getDatum() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(kalendar.getTime());
    }

    private String getVrijeme() {
        return String.format(Locale.getDefault(), "%02d:%02d",
                kalendar.get(Calendar.HOUR_OF_DAY), kalendar.get(Calendar.MINUTE));
    }

    private void prikaziDatumIVrijeme() {
        tvDatum.setText(getDatum());
        tvVrijeme.setText(getVrijeme());
    }

    private void odaberiDatum() {
        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        kalendar.set(Calendar.YEAR, year);
                        kalendar.set(Calendar.MONTH, month);
                        kalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        prikaziDatumIVrijeme();
                    }
                },
                kalendar.get(Calendar.YEAR),
                kalendar.get(Calendar.MONTH),
                kalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void odaberiVrijeme() {
        new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        kalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        kalendar.set(Calendar.MINUTE, minute);
                        prikaziDatumIVrijeme();
                    }
                },
                kalendar.get(Calendar.HOUR_OF_DAY),
                kalendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void spremi() {
        if (spJelo.getSelectedItem() == null) {
            Toast.makeText(this, "Odaberite jelo", Toast.LENGTH_SHORT).show();
            return;
        }

        String kolicinaStr = etKolicina.getText().toString().trim();
        if (kolicinaStr.isEmpty()) {
            Toast.makeText(this, "Unesite količinu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int kolicina = Integer.parseInt(kolicinaStr);
            if (kolicina <= 0) {
                Toast.makeText(this, "Količina mora biti pozitivna", Toast.LENGTH_SHORT).show();
                return;
            }

            Jelo odabranoJelo = (Jelo) spJelo.getSelectedItem();
            boolean uspjeh = dbHelper.azurirajPotrosnju(unosId, odabranoJelo.getId(),
                    getDatum(), getVrijeme(), kolicina);

            if (uspjeh) {
                Toast.makeText(this, "Promjene spremljene", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Greška pri spremanju promjena", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Unesite valjanu količinu", Toast.LENGTH_SHORT).show();
        }
    }

    private void potvrdiBrisanje() {
        new AlertDialog.Builder(this)
                .setTitle("Potvrdi brisanje")
                .setMessage("Jeste li sigurni da želite izbrisati ovaj unos?")
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.izbrisiPotrosnju(unosId);
                        Toast.makeText(UrediUnosActivity.this,
                                "Unos obrisan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Ne", null)
                .show();
    }
}
