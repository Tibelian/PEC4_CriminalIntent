package com.jcarlosprofesor.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jcarlosprofesor.criminalintent.database.CrimeBaseHelper;
import com.jcarlosprofesor.criminalintent.database.CrimeCursorWrapper;
import com.jcarlosprofesor.criminalintent.database.CrimeDbSchema;
import com.jcarlosprofesor.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //private List<Crime> mCrimes;

    private CrimeLab(Context context){
        //Código para abrir la base de datos.
        mContext = context.getApplicationContext();
        //En la llamada al metodo getWritableDatabase, CrimeBaseHelper hace:
        //1ºAbre /data/data/com.jcarlosprofesor.android.criminalintent/databases/crimeBase.db
        //2º Si es la primera vez que se crea la base de datos llama a onCreate(SQLiteDataBase)
        //3º Si no es la primera vez, comprueba el numero de versión en la base de datos
        //si el número de versión en CrimeOpenHelper es mayor, llama a onUpgrade y lo actualiza

        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        //mCrimes = new ArrayList<>();
    }
    public List<Crime> getCrimes(){

        //return mCrimes;
        //return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        /*for(Crime crime : mCrimes){
            if(crime.getId().equals(id)){
                return crime;
            }
        }*/
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }
    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return  sCrimeLab;
    }
    //Metodo que se encarga de pasar un Crime a una instancia de ContentValues
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved() ?1:0);
        //añadimos la nueva columna del objeto Crime
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }
    public void addCrime(Crime c){
        //mCrimes.add(c);
        //Ahora que ya tenemos el objeto ContentValues ya podemos añadir filas
        //a la base de datos.
        //Primero creamos el "paquete" ContentValues y despues lo añadimos
        //a la base de datos
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    //Metodo para actualizar un crimen
    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /*private Cursor queryCrimes(String whereClause, String[] whereArgs)*/
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        //return cursor;
        return new CrimeCursorWrapper(cursor);
    }
    //Metodo para proporcionar una ruta de almacenamiento para la foto
    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
    }

}
