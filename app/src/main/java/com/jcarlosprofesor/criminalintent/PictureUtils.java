package com.jcarlosprofesor.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

//Clase que nos va a permitir trabajar con las imagenes y escalarlas
//de forma adecuada
public class PictureUtils {

    //Metodo para estimar el tamaño del PhotoView
    public static Bitmap getScaledBitmap (String path, Activity activity){
        //Este metodo comprueba el tamaño de pantalla y reduce el de la imagen
        //hasta que ambos coinciden
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path,size.x,size.y);
    }

    //Metodo para escalar las imagenes
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        //Leemos las dimensiones de la imagen en disco
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float scrWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Averiguamos cuanto reducir el tamaño
        int intSampleSize = 1;
        if(srcHeight>destHeight || scrWidth>destWidth){
            float heightScale = srcHeight / destHeight;
            float widthScale = scrWidth / destWidth;

            intSampleSize = Math.round(heightScale>widthScale?heightScale:
                    widthScale);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = intSampleSize;

        //Volvemos a leerlo y creamos el mapa de bits definitivo
        return BitmapFactory.decodeFile(path,options);
    }
}
