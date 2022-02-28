package com.jcarlosprofesor.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

// MEJORA 3 --> selector de la hora
public class TimePickerFragment extends DialogFragment {

    // constantes para poder pasar
    // parámetros de una instancia a otra
    public static final String EXTRA_TIME = "intent_time";
    private static final String ARG_TIME = "time";

    // instancia el objeto con un argumento Date
    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // al crear la ventana del selector obtenemos
        // el argumento que se habrá recibido al instaciar este objeto
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // evento que devolverá el tiempo
        // seleccionado en el momento que el
        // el usuario haga click en el botón
        TimePickerDialog.OnTimeSetListener updateTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Date time = new Date();
                time.setHours(hourOfDay);
                time.setMinutes(minute);
                // devuelve el tiempo como un parámetro a la instancia padre
                sendResult(Activity.RESULT_OK, time);
            }
        };

        // inicializa el selector con el evento creado
        // anteriormente y con la hora actual
        TimePickerDialog dialog = new TimePickerDialog(
                getActivity(),
                updateTimeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        // título de la ventana
        dialog.setTitle(R.string.time_picker_title);

        // muestra la ventana
        dialog.show();

        // devuelve el objeto para cumplir con la herencia
        return dialog;
    }

    // método para devolver el tiempo
    // en el momento de guardar la selección
    private void sendResult(int resultCode, Date time){

        if(getTargetFragment() ==null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), resultCode, intent
        );

    }

}