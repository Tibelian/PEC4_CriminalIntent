package com.jcarlosprofesor.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jcarlosprofesor.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;
import com.jcarlosprofesor.criminalintent.database.CrimeDbSchema.CrimeTable;

//Clase creada para empaquetar los métodos de tratamiento del cursor
//CursorWrapper nos permite envolver la clase Cursor y añadirle nuevos metodos

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    //Añadimos el metodo getCrime que extraerá los datos de las columnas
    //relevantes
    public Crime getCrime(){
        //Obtenemos el contenido de las columnas
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        //leemos la nueva columna en el cursor
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        //Ahora vamos a crear el objeto Crime a devolver
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        //Seteamos la nueva columna leida, en el objeto Crime
        crime.setSuspect(suspect);
        return crime;

    }
}
