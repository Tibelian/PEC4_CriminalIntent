package com.jcarlosprofesor.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    //añadimos el campo suspect a nuestro modelo de datos
    private String mSuspect;

    //Modificamos el constructor para que funcione de manera adecuada
    //al cursor que hemos creado.
    public Crime(){
        this (UUID.randomUUID());
        //mId = UUID.randomUUID();
        //mDate = new Date();
    }
    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }


    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }
    public boolean isSolved(){
        return mSolved;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    public String getSuspect(){
        return mSuspect;
    }
    public void setSuspect(String suspect){
        mSuspect = suspect;
    }
    //Metodo para obtener un nombre de fichero bien formado
    public String getPhotoFilename(){
        return "IMG_"+getId().toString()+".jpg";
    }
}
