package com.jcarlosprofesor.criminalintent.database;

//La clase de contratos es un contenedor de constantes que
// definen nombres de URI, tablas y columnas
public class CrimeDbSchema {
    //Clase interna que define la estructura de nuestra tabla
    //Caso de necesitar mas tablas creariamos una clase interna para cada tabla
    public static final class CrimeTable{
        //Especificamos el nombre de la table dentro de la BD
        public static final String NAME = "crimes";
        //Describimos las columnas que forman parte de la tabla
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            //añadimos el nuevo campo a la tabla
            public static final String SUSPECT = "suspect";
        }
    }

}
