package com.example.healthapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "health.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_JELA = "jela";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAZIV = "naziv";
    private static final String COLUMN_KALORIJE = "kalorije";
    private static final String COLUMN_SLIKA = "slika_putanja";

    private static final String TABLE_POTROSNJA = "potrosnja";
    private static final String COLUMN_POTROSNJA_ID = "id";
    private static final String COLUMN_JELO_ID = "jelo_id";
    private static final String COLUMN_DATUM = "datum";
    private static final String COLUMN_VRIJEME = "vrijeme";
    private static final String COLUMN_KOLICINA = "kolicina";

    private static final String TABLE_LIMIT = "dnevni_limit";
    private static final String COLUMN_LIMIT_ID = "id";
    private static final String COLUMN_LIMIT_DATUM = "datum";
    private static final String COLUMN_LIMIT_VRIJEDNOST = "limit_vrijednost";

    /** Nazivi slika koje korisnik moze odabrati (datoteke u res/drawable). */
    public static final String[] SLIKE = {"jelo1", "jelo2", "jelo3", "jelo4", "jelo5", "jelo6"};

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_JELA_TABLE = "CREATE TABLE " + TABLE_JELA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAZIV + " TEXT NOT NULL,"
                + COLUMN_KALORIJE + " INTEGER NOT NULL,"
                + COLUMN_SLIKA + " TEXT DEFAULT 'jelo1'" + ")";
        db.execSQL(CREATE_JELA_TABLE);

        String CREATE_POTROSNJA_TABLE = "CREATE TABLE " + TABLE_POTROSNJA + "("
                + COLUMN_POTROSNJA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_JELO_ID + " INTEGER,"
                + COLUMN_DATUM + " TEXT NOT NULL,"
                + COLUMN_VRIJEME + " TEXT NOT NULL DEFAULT '12:00',"
                + COLUMN_KOLICINA + " INTEGER DEFAULT 1,"
                + "FOREIGN KEY(" + COLUMN_JELO_ID + ") REFERENCES " + TABLE_JELA + "(" + COLUMN_ID + ")" + ")";
        db.execSQL(CREATE_POTROSNJA_TABLE);

        String CREATE_LIMIT_TABLE = "CREATE TABLE " + TABLE_LIMIT + "("
                + COLUMN_LIMIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LIMIT_DATUM + " TEXT NOT NULL UNIQUE,"
                + COLUMN_LIMIT_VRIJEDNOST + " INTEGER NOT NULL" + ")";
        db.execSQL(CREATE_LIMIT_TABLE);

        dodajPocetnaJela(db);
    }

    private void dodajPocetnaJela(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        String[] nazivi = {
                "Tost s avokadom i jajem",
                "Zdjela s piletinom i povrćem",
                "Krem juha od rajčice",
                "Losos na žaru s povrćem",
                "Tjestenina s pestom",
                "Vege zdjela sa slanutkom"};
        int[] kalorije = {320, 520, 210, 480, 610, 430};

        for (int i = 0; i < nazivi.length; i++) {
            values.clear();
            values.put(COLUMN_NAZIV, nazivi[i]);
            values.put(COLUMN_KALORIJE, kalorije[i]);
            // sprema se samo naziv datoteke iz res/drawable, npr. "jelo1"
            values.put(COLUMN_SLIKA, SLIKE[i]);
            db.insert(TABLE_JELA, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POTROSNJA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIMIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JELA);
        onCreate(db);
    }

    // ---------- JELA ----------

    public boolean dodajJelo(String naziv, int kalorije) {
        return dodajJelo(naziv, kalorije, SLIKE[0]);
    }

    public boolean dodajJelo(String naziv, int kalorije, String slikaPutanja) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAZIV, naziv);
        values.put(COLUMN_KALORIJE, kalorije);
        values.put(COLUMN_SLIKA, slikaPutanja);

        long result = db.insert(TABLE_JELA, null, values);
        return result != -1;
    }

    public boolean azurirajJelo(int id, String naziv, int kalorije, String slikaPutanja) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAZIV, naziv);
        values.put(COLUMN_KALORIJE, kalorije);
        values.put(COLUMN_SLIKA, slikaPutanja);

        int result = db.update(TABLE_JELA, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        return result > 0;
    }

    public List<Jelo> dohvatiSvaJela() {
        List<Jelo> jela = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_JELA,
                new String[]{COLUMN_ID, COLUMN_NAZIV, COLUMN_KALORIJE, COLUMN_SLIKA},
                null, null, null, null, COLUMN_NAZIV);

        if (cursor.moveToFirst()) {
            do {
                Jelo jelo = new Jelo();
                jelo.setId(cursor.getInt(0));
                jelo.setNaziv(cursor.getString(1));
                jelo.setKalorije(cursor.getInt(2));
                jelo.setSlikaPutanja(cursor.isNull(3) ? SLIKE[0] : cursor.getString(3));
                jela.add(jelo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return jela;
    }

    /** Dohvaca jedno jelo po ID-u ili null ako jelo ne postoji. */
    public Jelo dohvatiJelo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_JELA,
                new String[]{COLUMN_ID, COLUMN_NAZIV, COLUMN_KALORIJE, COLUMN_SLIKA},
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        Jelo jelo = null;
        if (cursor.moveToFirst()) {
            jelo = new Jelo();
            jelo.setId(cursor.getInt(0));
            jelo.setNaziv(cursor.getString(1));
            jelo.setKalorije(cursor.getInt(2));
            jelo.setSlikaPutanja(cursor.isNull(3) ? SLIKE[0] : cursor.getString(3));
        }
        cursor.close();
        return jelo;
    }

    public boolean izbrisiJelo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // prvo se brisu unosi tog jela da u dnevniku ne ostanu prazni zapisi
        db.delete(TABLE_POTROSNJA, COLUMN_JELO_ID + " = ?", new String[]{String.valueOf(id)});
        int result = db.delete(TABLE_JELA, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // ---------- UNOSI (POTROSNJA) ----------

    public boolean dodajPotrosnju(int jeloId, String datum, String vrijeme, int kolicina) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_JELO_ID, jeloId);
        values.put(COLUMN_DATUM, datum);
        values.put(COLUMN_VRIJEME, vrijeme);
        values.put(COLUMN_KOLICINA, kolicina);

        long result = db.insert(TABLE_POTROSNJA, null, values);
        return result != -1;
    }

    public boolean azurirajPotrosnju(int id, int jeloId, String datum, String vrijeme, int kolicina) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_JELO_ID, jeloId);
        values.put(COLUMN_DATUM, datum);
        values.put(COLUMN_VRIJEME, vrijeme);
        values.put(COLUMN_KOLICINA, kolicina);

        int result = db.update(TABLE_POTROSNJA, values, COLUMN_POTROSNJA_ID + " = ?",
                new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean izbrisiPotrosnju(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_POTROSNJA, COLUMN_POTROSNJA_ID + " = ?",
                new String[]{String.valueOf(id)});
        return result > 0;
    }

    /** Svi unosi izmedu dva datuma (ukljucivo), najnoviji prvi. */
    public List<Unos> dohvatiUnose(String odDatuma, String doDatuma) {
        List<Unos> unosi = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p." + COLUMN_POTROSNJA_ID + ", p." + COLUMN_JELO_ID + ", "
                + "j." + COLUMN_NAZIV + ", j." + COLUMN_KALORIJE + ", "
                + "p." + COLUMN_DATUM + ", p." + COLUMN_VRIJEME + ", p." + COLUMN_KOLICINA + " "
                + "FROM " + TABLE_POTROSNJA + " p "
                + "INNER JOIN " + TABLE_JELA + " j ON p." + COLUMN_JELO_ID + " = j." + COLUMN_ID + " "
                + "WHERE p." + COLUMN_DATUM + " BETWEEN ? AND ? "
                + "ORDER BY p." + COLUMN_DATUM + " DESC, p." + COLUMN_VRIJEME + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{odDatuma, doDatuma});
        if (cursor.moveToFirst()) {
            do {
                Unos unos = new Unos();
                unos.setId(cursor.getInt(0));
                unos.setJeloId(cursor.getInt(1));
                unos.setJeloNaziv(cursor.getString(2));
                unos.setKalorijeJela(cursor.getInt(3));
                unos.setDatum(cursor.getString(4));
                unos.setVrijeme(cursor.getString(5));
                unos.setKolicina(cursor.getInt(6));
                unosi.add(unos);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return unosi;
    }

    // ---------- STATISTIKA ----------

    /** Ukupne kalorije za razdoblje od-do (datumi ukljucivo). */
    public int ukupneKalorijeRazdoblje(String odDatuma, String doDatuma) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(j." + COLUMN_KALORIJE + " * p." + COLUMN_KOLICINA + ") "
                + "FROM " + TABLE_POTROSNJA + " p "
                + "INNER JOIN " + TABLE_JELA + " j ON p." + COLUMN_JELO_ID + " = j." + COLUMN_ID + " "
                + "WHERE p." + COLUMN_DATUM + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{odDatuma, doDatuma});
        int ukupno = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            ukupno = cursor.getInt(0);
        }
        cursor.close();
        return ukupno;
    }

    /** Ukupne kalorije za jedan dan. */
    public int ukupneKalorijeDan(String datum) {
        return ukupneKalorijeRazdoblje(datum, datum);
    }

    /** Broj razlicitih dana u razdoblju u kojima postoji barem jedan unos. */
    public int brojDanaSUnosom(String odDatuma, String doDatuma) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(DISTINCT " + COLUMN_DATUM + ") FROM " + TABLE_POTROSNJA
                + " WHERE " + COLUMN_DATUM + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{odDatuma, doDatuma});
        int broj = 0;
        if (cursor.moveToFirst()) {
            broj = cursor.getInt(0);
        }
        cursor.close();
        return broj;
    }

    // ---------- DNEVNI LIMIT ----------

    public boolean postaviDnevniLimit(String datum, int limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIMIT_DATUM, datum);
        values.put(COLUMN_LIMIT_VRIJEDNOST, limit);

        long result = db.insertWithOnConflict(TABLE_LIMIT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public int dohvatiDnevniLimit(String datum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LIMIT, new String[]{COLUMN_LIMIT_VRIJEDNOST},
                COLUMN_LIMIT_DATUM + " = ?", new String[]{datum}, null, null, null);

        int limit = 2000;
        if (cursor.moveToFirst()) {
            limit = cursor.getInt(0);
        }
        cursor.close();
        return limit;
    }

    // ---------- POMOCNE METODE ----------

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
