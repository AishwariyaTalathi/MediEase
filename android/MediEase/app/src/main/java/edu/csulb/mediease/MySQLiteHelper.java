package edu.csulb.mediease;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "mediease";
    private static final String COLUMN_NAMES = "names";
    private static final String COLUMN_FREQUENCY = "frequency";
    private static final String COLUMN_TIMES = "times";
    private static final String DATABASE_NAME = "meds.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    Medicine getRow(int id) {
        System.out.println("db get row position = " + id);
        SQLiteDatabase db = this.getReadableDatabase();
        Medicine medicine = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{"_id", "names", "frequency", "times"},
                "_id = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            medicine = new Medicine(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            cursor.close();
        }

        db.close();
        return medicine;
    }

    //update time
    void insertTime(int id, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIMES, time);
        db.update(TABLE_NAME, contentValues, "_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // insert single medicine
    void insertData(Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAMES, medicine.getName());
        contentValues.put(COLUMN_FREQUENCY, medicine.getFrequency());
        contentValues.put(COLUMN_TIMES, "Set Time");

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    // get row count
    int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);

        db.close();
        return (int) count;
    }

    // delete single row from the table
    void deleteRow(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // get all entries in the table
    List<Medicine> getData() {
        List<Medicine> medicines = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Medicine medicine = new Medicine();
                medicine.setId(cursor.getInt(0));
                medicine.setName(cursor.getString(1));
                medicine.setFrequency(cursor.getString(2));
                medicine.setTimes(cursor.getString(3));
                medicines.add(medicine);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicines;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY, names TEXT, frequency TEXT, times TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
