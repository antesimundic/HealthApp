package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class PopisJelaActivity extends AppCompatActivity {

    private ListView lvJela;
    private TextView tvPrazno;
    private DatabaseHelper dbHelper;
    private List<Jelo> jela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popis_jela);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        lvJela = findViewById(R.id.lvJela);
        tvPrazno = findViewById(R.id.tvPraznoPopis);

        lvJela.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Jelo jelo = jela.get(position);

                Intent intent = new Intent(PopisJelaActivity.this, ProfilJelaActivity.class);
                intent.putExtra("jelo_id", jelo.getId());
                startActivity(intent);
            }
        });
    }

    private void ucitajJela() {
        jela = dbHelper.dohvatiSvaJela();
        lvJela.setAdapter(new JeloAdapter(this, jela));
        tvPrazno.setVisibility(jela.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ucitajJela();
    }
}
