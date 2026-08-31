package com.example.healthapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;

/** Prikazuje jedan unos u dnevniku: vrijeme, jelo, datum, kolicinu i kalorije. */
public class UnosAdapter extends ArrayAdapter<Unos> {

    public UnosAdapter(Context context, List<Unos> unosi) {
        super(context, R.layout.item_unos, unosi);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View redak = convertView;
        if (redak == null) {
            redak = LayoutInflater.from(getContext()).inflate(R.layout.item_unos, parent, false);
        }

        Unos unos = getItem(position);
        if (unos != null) {
            TextView tvVrijeme = redak.findViewById(R.id.tvVrijemeStavke);
            TextView tvNaziv = redak.findViewById(R.id.tvNazivUnosa);
            TextView tvDetalji = redak.findViewById(R.id.tvDetaljiUnosa);
            TextView tvKalorije = redak.findViewById(R.id.tvKalorijeUnosa);

            tvVrijeme.setText(unos.getVrijeme());
            tvNaziv.setText(unos.getJeloNaziv());
            tvDetalji.setText(unos.getDatum() + "  •  " + unos.getKolicina() + " x " + unos.getKalorijeJela() + " kcal");
            tvKalorije.setText(unos.getUkupnoKalorija() + " kcal");
        }

        return redak;
    }
}
