package com.jcarlosprofesor.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Clase que va a definir la instancia de nuestro dialogo
public class DatePickerFragment extends DialogFragment {

    //Creamos la constante para identificar la fecha dentro del bundle
    //de argumentos del fragment
    private static  final String ARG_DATE = "date";
    //Creamos la constante para identificar la fecha que pasamos en el intent
    public static final String EXTRA_DATE = "intent_date";
    private DatePicker mDatePicker;

    //Creamos el metodo newInstance para pasar el bundle
    //de parametros.
    public static DatePickerFragment newInstante(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //Sobreescribimos el metodo onCreateDialog, será llamado como parte del proceso
    //para presentar el DialogFragment en pantalla
    //Este método llama a una interfaz fluida que:
    //Usa la clase AlertDialog para construir una instancia de AlertDialog
    //En primer lugar pasamos un Context al constructor de AlertDialog
    //despues invocamos dos metodos de AlertDialog.Builder para configurar
    //el dialogo.
    //El  metodo setPositiveButton acepta un id de cadena, y un objeto que implementa
    //onClickListener (momentaneamente a null)
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Para inicializar el DataPicker con la fecha del objeto Date
        //DataPicker necesita numeros enteros para el mes, dia y año
        //Debemos tratar el objeto Date para obtener estos números y
        //luego asignarlos
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date,null);
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_data_picker);
        mDatePicker.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,month,day).getTime();
                                sendResult(Activity.RESULT_OK,date);
                            }
                        })
                .create();
    }
    private void sendResult(int resultCode, Date date){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

}
