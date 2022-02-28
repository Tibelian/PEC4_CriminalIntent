package com.jcarlosprofesor.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    //Boton de envio del informe
    private Button mReportButton;
    //Boton para buscar el sospechoso
    private Button mSuspectButton;
    //Boton para la camara
    private ImageButton mPhotoButton;
    //Imagen sospechoso
    private ImageView mPhotoView;
    //variable para la ruta de la foto
    private File mPhotoFile;
    //Creamos la variable que implementara la interface.
    private Callbacks mCallbacks;

    /*Interface requerida para las activity que quieran albergar un fragment.*/
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }
    private static  final String ARG_CRIME_ID = "crime_id";
    //Constante que nos va a etiquetar el DataPickerFragment a mostrar
    private static final String DIALOG_DATE = "DialogDate";
    //Constante para el código de petición
    private static final int REQUEST_DATE = 0;
    //Constante para el código de peticion del contacto
    private static final int REQUEST_CONTACT = 1;
    //Constante para la peticion de tomar una foto
    private static final int REQUEST_PHOTO = 2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mCrime = new Crime();*/
        /*UUID crimeId = (UUID) getActivity().getIntent()
                .getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);*/
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //Instanciamos la variable para la ruta de la foto
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container,false);
        //Conectamos las variables de instancia de la camara y de la imagen con sus elementos en el layout
        mPhotoView = (ImageView)view.findViewById(R.id.crime_photo);
        //Invocamos el metodo para cargar la imagen
        updatePhotoView();
        mPhotoButton = (ImageButton)view.findViewById(R.id.crime_camera);
        //Creamos el intent que da tratamiento a la toma de la foto
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.jcarlosprofesor.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity:cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        mTitleField = (EditText) view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        //Establecemos el listener que nos mostrara el Dialog
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstante(mCrime.getDate());
                //Establecemos el fragment destino
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fragmentManager,DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
        //Creamos una referencia para el boton de envio del informe
        //En la implementación de su listener añadimos el intent implicito de envio de text
        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                //Creamos un chooser para asegurarnos que el usuario usa
                //siempre le aparezcan opciones de eleccion
                intent = Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);
            }
        });
        //Codigo para dar comportamiento al boton
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if(mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        //Comprobamos que las activity responden a los intent
       /* PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }*/
        return view;
    }
    //Sobreescribimos el método onActivityResult para recuperar el extra, fijamos la fecha en el objeto Crime
    //y actualizamos el texto del boton

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            mDateButton.setText(mCrime.getDate().toString());
        }
        //añadimos el tratamiento del resultado devuelto por la app Contactos
        else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            //Especificamos el campo para el que queremos que la consulta
            //devuelva valores
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Ejecutamos la consulta
            Cursor c = getActivity().getContentResolver().query(
                    contactUri,queryFields,null,null,null);
            try{
                //Comprobamos que hemos obtenidos resultados
                if(c.getCount() == 0){
                    return;
                }
                //Extraemos la primera columna
                //Es el nombre del sospechoso
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.jcarlosprofesor.criminalintent.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }
    }

    //Sobreescribimos el metodo onPause para asegurarnos que las instancias
    //modificadas de Crime son guardadas antes de que CrimeFragment finalice

    @Override
    public void onPause() {
        super.onPause();
        //Llamamos al método que hemos implemantado en CrimeLab para actualizar
        //un crimen
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    //metodo que nos va a construir el informe de un crime concreto en ejecucion
    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }
        else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }
    //metodo para cargat el objeto Bitmap en el ImageView
    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}
