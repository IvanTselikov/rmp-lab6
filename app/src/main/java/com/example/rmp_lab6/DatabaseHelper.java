package com.example.rmp_lab6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "tasks"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATETIME = "dateTime";
    public static final String COLUMN_HASTIME = "hasTime";
    public static final String COLUMN_PRIORITY = "priority";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                COLUMN_NUMBER + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATETIME + " TEXT, "+
                COLUMN_HASTIME+ " INTEGER, " +
                COLUMN_PRIORITY + " INTEGER)"; // TODO: точка с запятой
        db.execSQL(query); // выполняем запрос создания в БД таблицы
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void clearDataBase(SQLiteDatabase db) {
        onUpgrade(db, SCHEMA, SCHEMA);
    }

//    public void deleteDataBase(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
//    }
}
