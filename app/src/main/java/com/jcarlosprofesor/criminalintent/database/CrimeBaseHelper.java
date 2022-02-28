package com.jcarlosprofesor.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.jcarlosprofesor.criminalintent.database.CrimeDbSchema.CrimeTable;
//Clase que nos facilita la apertura y tratamiento de la base de datos.

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }
    //Codigo dedicado para crear la base de datos inicial
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + "(" +
            "_id integer primary key autoincrement, " +
            CrimeTable.Cols.UUID + ", "+
            CrimeTable.Cols.TITLE + ", "+
            CrimeTable.Cols.DATE + ", "+
            CrimeTable.Cols.SOLVED + ","+
            //añadimos la nueva columna en la tabla
            CrimeTable.Cols.SUSPECT +
        ")"
        );
    }
    //Código destinado a gestionar cualquier actualización
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
