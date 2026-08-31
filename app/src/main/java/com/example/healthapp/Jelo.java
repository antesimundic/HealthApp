package com.example.healthapp;

public class Jelo {
    private int id;
    private String naziv;
    private int kalorije;
    private String slikaPutanja;

    public Jelo() {
        this.slikaPutanja = "jelo1";
    }

    public Jelo(String naziv, int kalorije) {
        this.naziv = naziv;
        this.kalorije = kalorije;
        this.slikaPutanja = "jelo1";
    }

    public Jelo(int id, String naziv, int kalorije, String slikaPutanja) {
        this.id = id;
        this.naziv = naziv;
        this.kalorije = kalorije;
        this.slikaPutanja = slikaPutanja;
    }

    public int getId() { return id; }
    public String getNaziv() { return naziv; }
    public int getKalorije() { return kalorije; }
    public String getSlikaPutanja() { return slikaPutanja; }

    public void setId(int id) { this.id = id; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public void setKalorije(int kalorije) { this.kalorije = kalorije; }
    public void setSlikaPutanja(String slikaPutanja) { this.slikaPutanja = slikaPutanja; }

    @Override
    public String toString() {
        return naziv + " (" + kalorije + " kcal)";
    }
}
