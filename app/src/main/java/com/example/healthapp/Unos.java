package com.example.healthapp;

/**
 * Jedan unos u dnevnik: koje jelo je pojedeno, kojeg datuma, u koje vrijeme
 * i u kojoj kolicini.
 */
public class Unos {
    private int id;
    private int jeloId;
    private String jeloNaziv;
    private int kalorijeJela;
    private String datum;
    private String vrijeme;
    private int kolicina;

    public int getId() { return id; }
    public int getJeloId() { return jeloId; }
    public String getJeloNaziv() { return jeloNaziv; }
    public int getKalorijeJela() { return kalorijeJela; }
    public String getDatum() { return datum; }
    public String getVrijeme() { return vrijeme; }
    public int getKolicina() { return kolicina; }

    public void setId(int id) { this.id = id; }
    public void setJeloId(int jeloId) { this.jeloId = jeloId; }
    public void setJeloNaziv(String jeloNaziv) { this.jeloNaziv = jeloNaziv; }
    public void setKalorijeJela(int kalorijeJela) { this.kalorijeJela = kalorijeJela; }
    public void setDatum(String datum) { this.datum = datum; }
    public void setVrijeme(String vrijeme) { this.vrijeme = vrijeme; }
    public void setKolicina(int kolicina) { this.kolicina = kolicina; }

    /** Ukupne kalorije ovog unosa (kalorije jela * broj porcija). */
    public int getUkupnoKalorija() {
        return kalorijeJela * kolicina;
    }

    @Override
    public String toString() {
        return datum + " u " + vrijeme + "\n"
                + jeloNaziv + "  x" + kolicina + "  =  " + getUkupnoKalorija() + " kcal";
    }
}
