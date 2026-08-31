package com.example.healthapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Zaslon za dodavanje novog jela. Ako se u Intentu posalje "jelo_id",
 * isti zaslon sluzi za uredivanje postojeceg jela.
 */
public class DodajJeloActivity extends AppCompatActivity {

    private EditText etNazivJela, etKalorije;
    private Spinner spSlika;
    private Button btnSpremi;
    private ImageView ivPregledSlike;
    private TextView tvNaslov;
    private DatabaseHelper dbHelper;

    /** -1 znaci da se dodaje novo jelo, inace se uredjuje postojece. */
    private int jeloId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_jelo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        tvNaslov = findViewById(R.id.tvNaslov);
        etNazivJela = findViewById(R.id.etNazivJela);
        etKalorije = findViewById(R.id.etKalorije);
        spSlika = findViewById(R.id.spSlika);
        btnSpremi = findViewById(R.id.btnSpremi);
        ivPregledSlike = findViewById(R.id.ivPregledSlike);

        pripremiSpinnerSlika();

        jeloId = getIntent().getIntExtra("jelo_id", -1);
        if (jeloId != -1) {
            pripremiUredivanje();
        }

        btnSpremi.setOnClickListener(v -> spremi());
    }

    /**
     * Ovaj zaslon otvara se s pocetnog zaslona i iz profila jela,
     * pa strelica natrag jednostavno zatvara zaslon.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void pripremiSpinnerSlika() {
        String[] opisi = new String[DatabaseHelper.SLIKE.length];
        for (int i = 0; i < opisi.length; i++) {
            opisi[i] = "Slika " + (i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, opisi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSlika.setAdapter(adapter);

        // pregled se mijenja zajedno s odabirom u spinneru
        spSlika.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ivPregledSlike.setImageResource(
                        SlikaUtil.idSlike(DodajJeloActivity.this, DatabaseHelper.SLIKE[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** Popunjava polja podacima jela koje se uredjuje. */
    private void pripremiUredivanje() {
        Jelo jelo = dbHelper.dohvatiJelo(jeloId);
        if (jelo == null) {
            Toast.makeText(this, "Jelo više ne postoji", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("Uredi jelo");
        tvNaslov.setText("Uredi jelo");
        btnSpremi.setText("Spremi promjene");

        etNazivJela.setText(jelo.getNaziv());
        etKalorije.setText(String.valueOf(jelo.getKalorije()));
        spSlika.setSelection(pronadiIndeksSlike(jelo.getSlikaPutanja()));
    }

    private int pronadiIndeksSlike(String nazivSlike) {
        for (int i = 0; i < DatabaseHelper.SLIKE.length; i++) {
            if (DatabaseHelper.SLIKE[i].equals(nazivSlike)) {
                return i;
            }
        }
        return 0;
    }

    private void spremi() {
        String naziv = etNazivJela.getText().toString().trim();
        String kalorijeText = etKalorije.getText().toString().trim();

        if (naziv.isEmpty() || kalorijeText.isEmpty()) {
            Toast.makeText(this, "Unesite sve podatke", Toast.LENGTH_SHORT).show();
            return;
        }

        int kalorije;
        try {
            kalorije = Integer.parseInt(kalorijeText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Unesite valjani broj kalorija", Toast.LENGTH_SHORT).show();
            return;
        }

        if (kalorije <= 0) {
            Toast.makeText(this, "Kalorije moraju biti veće od nule", Toast.LENGTH_SHORT).show();
            return;
        }

        String odabranaSlika = DatabaseHelper.SLIKE[spSlika.getSelectedItemPosition()];

        if (jeloId == -1) {
            if (dbHelper.dodajJelo(naziv, kalorije, odabranaSlika)) {
                Toast.makeText(this, "Jelo dodano", Toast.LENGTH_SHORT).show();
                etNazivJela.setText("");
                etKalorije.setText("");
                etNazivJela.requestFocus();
            } else {
                Toast.makeText(this, "Greška pri spremanju jela", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.azurirajJelo(jeloId, naziv, kalorije, odabranaSlika)) {
                Toast.makeText(this, "Promjene spremljene", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Greška pri spremanju promjena", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
