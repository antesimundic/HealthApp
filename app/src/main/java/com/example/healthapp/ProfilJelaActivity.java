package com.example.healthapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilJelaActivity extends AppCompatActivity {

    private ImageView ivSlika;
    private TextView tvNaziv, tvKalorije;
    private Button btnUredi, btnIzbrisi;
    private DatabaseHelper dbHelper;
    private int jeloId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_jela);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        ivSlika = findViewById(R.id.ivSlikaJela);
        tvNaziv = findViewById(R.id.tvNazivJela);
        tvKalorije = findViewById(R.id.tvKalorijeJela);
        btnUredi = findViewById(R.id.btnUredi);
        btnIzbrisi = findViewById(R.id.btnIzbrisi);

        jeloId = getIntent().getIntExtra("jelo_id", -1);

        btnUredi.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilJelaActivity.this, DodajJeloActivity.class);
            intent.putExtra("jelo_id", jeloId);
            startActivity(intent);
        });

        btnIzbrisi.setOnClickListener(v -> potvrdiBrisanje());
    }

    @Override
    protected void onResume() {
        super.onResume();
        prikaziJelo();
    }

    private void prikaziJelo() {
        Jelo jelo = dbHelper.dohvatiJelo(jeloId);
        if (jelo == null) {
            // jelo je u meduvremenu obrisano
            finish();
            return;
        }

        tvNaziv.setText(jelo.getNaziv());
        tvKalorije.setText(jelo.getKalorije() + " kcal po porciji");
        ivSlika.setImageResource(SlikaUtil.idSlike(this, jelo.getSlikaPutanja()));
    }

    private void potvrdiBrisanje() {
        new AlertDialog.Builder(this)
                .setTitle("Potvrdi brisanje")
                .setMessage("Brisanjem jela brišu se i svi njegovi unosi u dnevniku. Nastaviti?")
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.izbrisiJelo(jeloId)) {
                            Toast.makeText(ProfilJelaActivity.this,
                                    "Jelo uspješno obrisano", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ProfilJelaActivity.this,
                                    "Greška pri brisanju jela", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Ne", null)
                .show();
    }
}
