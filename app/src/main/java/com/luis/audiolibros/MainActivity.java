package com.luis.audiolibros;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int iniciado = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int idx = -1;
        if(getIntent().getExtras() != null ){
            idx = getIntent().getExtras().getInt("libroIdx", -1);
            Log.d("cosa", "Hola, hay un extra " + idx );
        }
        if(idx == -1){
            Log.d("cosa", "Hola No vengo de la noti" + idx);
            if (findViewById(R.id.contenedor_pequeno) != null &&
                    getSupportFragmentManager().findFragmentById(R.id.contenedor_pequeno)==null){
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SelectorFragment fragment = new SelectorFragment();
                fragmentTransaction.add(R.id.contenedor_pequeno, fragment); //cuando se agrega fragmento dinámicamente se le asigna el mismo ID que tiene su contenedor
                fragmentTransaction.commit();
            }
        } else {
            Log.d("cosa", "Hola Vengo de la notificacion");
            iniciado = 10;
            mostrarDetalle(idx);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /*RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdaptadorLibros adaptadorLibros;*/

    /*recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this,2); // numero columnas
        recyclerView.setLayoutManager(layoutManager);
        adaptadorLibros = new AdaptadorLibros(getApplicationContext(), Libro.ejemploLibros());
        recyclerView.setAdapter(adaptadorLibros);*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void mostrarDetalle(int index){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DetalleFragment detalleFragment = (DetalleFragment)
                fragmentManager.findFragmentById(R.id.detalle_fragment);

        if(detalleFragment!=null&&detalleFragment.isVisible()){
            Bundle bundle = new Bundle();
            bundle.putInt("iniciado", iniciado);
            detalleFragment.setArguments(bundle);
            detalleFragment.ponInfoLibro(index);
        }
        else{
            detalleFragment = new DetalleFragment();
            Bundle bundle = new Bundle(); //colección llave-valor
            bundle.putInt(DetalleFragment.ARG_ID_LIBRO, index);
            bundle.putInt("iniciado", iniciado);
            detalleFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.contenedor_pequeno, detalleFragment)
                    .addToBackStack(null).commit();
        }
    }
}