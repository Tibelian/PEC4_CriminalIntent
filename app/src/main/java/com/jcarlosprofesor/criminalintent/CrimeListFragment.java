package com.jcarlosprofesor.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class CrimeListFragment extends Fragment {

    // MEJORA 6 --> contenedor con el texto "there are no crimes"
    // y el botón para agregar crímenes
    private View mNoCrimes;
    private Button mAddCrime;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    //1º En primer lugar definimos una variable miembro que guarde un objeto que implemente
    //Callbacks
    private Callbacks mCallbacks;

    //2º Definimos la interface requerida para las activity que albergan al fragment
    public interface Callbacks{
        //Metodo que nos va a permitir comunicar el crimen seleccionado
        void onCrimeSelected(Crime crime);
    }
    //3º Sobreescribimos los metodos del ciclo de vida de los fragment.
    //En el metodo onAttach asignamos la activity
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

    //Indicamos al FragmentManager que su fragment va a recibir
    //una llamada al metodo onCreateOptionsMenu
    //Indicamos al FragmentManager que CrimeListFragment va a recibir
    //llamadas del menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;



        public CrimeHolder(LayoutInflater inflater, ViewGroup parent ){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved_img);
            itemView.setOnClickListener(this);

        }
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(formatDate(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved()?View.VISIBLE:View.GONE);
        }

        // MEJORA 1 --> se le cambia el formato a la fecha en el recycler view
        public String formatDate(Date dateCrime){
            DateFormat df = new DateFormat();
            String inFormat = "EEEE, MMM dd, yyyy";
            return df.format(inFormat, dateCrime).toString();
        }

        @Override
        public void onClick(View v) {
            /*Toast.makeText(getActivity(),
                            mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                            .show();*/
            //Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getId());
            /*Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
            startActivity(intent);*/
            mCallbacks.onCrimeSelected(mCrime);
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        //Metodo que reemplaza la lista de crimenes que se muestran
        public void setCrimes (List<Crime> crimes){
            mCrimes = crimes;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);

        // MEJORA 6 --> elementos que utilizaremos para informar
        // al usuario en caso de que no se encuentren crímenes
        mNoCrimes = view.findViewById(R.id.no_crimes_container);
        mAddCrime = view.findViewById(R.id.add_crime_button);

        return view;
    }

    // MEJORA 6 --> si no hay crímenes entonces muestra un mensaje
    // y un botón para agregar un nuevo crimen
    private void checkNumCrimes(List<Crime> crimes) {
        if (crimes.size() > 0) return;
        mNoCrimes.setVisibility(View.VISIBLE);
        mAddCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
            }
        });
    }

    public void onResume(){
        super.onResume();
        updateUI();
    }
    //hacemos public updateUI para que pueda ser llamado desde CrimeListActivity
    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        // MEJORA 6 --> cada vez que se actualiza la interfaz hay que comprobar
        // si tenemos o no crímenes para mostrar o no un mensaje informativo
        checkNumCrimes(crimes);

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{
            //Actualizamos la lista que debe usar el adaptador para mostrar los crimenes
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
    }

    //Inyectamos el archivo de layout del menu, en el objeto menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        //Actualizamos el menu
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                /*Intent intent = CrimePagerActivity
                        .newIntent(getActivity(),crime.getId());
                startActivity(intent);*/
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();

        // MEJORA 5 --> utiliza un texto diferente en caso de que el contador es distinto que 1
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if(!mSubtitleVisible){
            subtitle=null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}
