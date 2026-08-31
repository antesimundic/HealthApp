package com.example.healthapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;

/** Prikazuje jelo u popisu: slicica, naziv i kalorije. */
public class JeloAdapter extends ArrayAdapter<Jelo> {

    public JeloAdapter(Context context, List<Jelo> jela) {
        super(context, R.layout.item_jelo, jela);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View redak = convertView;
        if (redak == null) {
            redak = LayoutInflater.from(getContext()).inflate(R.layout.item_jelo, parent, false);
        }

        Jelo jelo = getItem(position);
        if (jelo != null) {
            ImageView ivSlika = redak.findViewById(R.id.ivSlikaStavke);
            TextView tvNaziv = redak.findViewById(R.id.tvNazivStavke);
            TextView tvKalorije = redak.findViewById(R.id.tvKalorijeStavke);

            ivSlika.setImageResource(SlikaUtil.idSlike(getContext(), jelo.getSlikaPutanja()));
            tvNaziv.setText(jelo.getNaziv());
            tvKalorije.setText(jelo.getKalorije() + " kcal");
        }

        return redak;
    }
}
