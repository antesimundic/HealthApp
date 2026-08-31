package com.example.healthapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class IzbrisiJeloActivity extends AppCompatActivity {

    private ListView lvJelaZaBrisanje;
    private TextView tvPrazno;
    private DatabaseHelper dbHelper;
    private List<Jelo> jela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izbrisi_jelo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        lvJelaZaBrisanje = findViewById(R.id.lvJelaZaBrisanje);
        tvPrazno = findViewById(R.id.tvPraznoBrisanje);

        ucitajJela();

        lvJelaZaBrisanje.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Jelo jelo = jela.get(position);

                new AlertDialog.Builder(IzbrisiJeloActivity.this)
                        .setTitle("Potvrdi brisanje")
                        .setMessage("Želite li obrisati jelo \"" + jelo.getNaziv()
                                + "\"? Brišu se i svi njegovi unosi u dnevniku.")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbHelper.izbrisiJelo(jelo.getId())) {
                                    Toast.makeText(IzbrisiJeloActivity.this,
                                            "Jelo uspješno obrisano", Toast.LENGTH_SHORT).show();
                                    ucitajJela();
                                } else {
                                    Toast.makeText(IzbrisiJeloActivity.this,
                                            "Greška pri brisanju jela", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Ne", null)
                        .show();
            }
        });
    }

    private void ucitajJela() {
        jela = dbHelper.dohvatiSvaJela();
        lvJelaZaBrisanje.setAdapter(new JeloAdapter(this, jela));
        tvPrazno.setVisibility(jela.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
