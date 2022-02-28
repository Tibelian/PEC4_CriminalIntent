package com.jcarlosprofesor.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
implements CrimeFragment.Callbacks{

    private static final String EXTRA_CRIME_ID = "crime_id";
    private ViewPager2 mViewPager;
    private List<Crime> mCrimes;

    // MEJORA 2 --> botones que usaremos para navegar por el viewPager
    private Button mFirst;
    private Button mLast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        //Traemos la lista de elemento Crime contenida en CrimeLab
        mCrimes = CrimeLab.get(this).getCrimes();
        //Creamos el objeto mViewPager que mostrara los crimenes
        mViewPager = (ViewPager2) findViewById(R.id.activity_crime_pager_view_pager);
        //Creamos el objeto FragmentManager para manejar los fragment que vamos a cargar
        //en el objeto mViewPager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //Seteamos el adaptador necesario para leer los objetos Crime de la lista
        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getItemCount() {
                return mCrimes.size();
            }
        });
        for(int i = 0; i < mCrimes.size(); i++){
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
            }
        }


        // MEJORA 2 --> se le agrega la acción a los botones
        // para poder saltar al primer o último elemento del viewPager
        mFirst = findViewById(R.id.first_button);
        mLast = findViewById(R.id.last_button);
        mFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });
        // cada vez que la el elemento cambia se ejecuta un callback
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // dentro de la función se verifica el índice
                // para saber si hay que deshabilitar
                // alguno de los botones
                mFirst.setClickable(position != 0);
                mLast.setClickable(position != (mCrimes.size() - 1));
                super.onPageSelected(position);
            }
        });


    }
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
