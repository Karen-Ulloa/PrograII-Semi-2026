package com.example.miprimerapp;
import java.io.File;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;
import android.content.Intent;
import java.util.ArrayList;

public class ListaDibujosActivity extends Activity {

    GridView gridDibujos;

    EditText txtBuscar;

    DB db;

    ArrayList<String> dibujos =
            new ArrayList<>();

    ArrayList<String> dibujosOriginal =
            new ArrayList<>();

    AdaptadorDibujos adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_lista_dibujos
        );

        db = new DB(this);


        gridDibujos =
                findViewById(R.id.gridDibujos);


        txtBuscar =
                findViewById(R.id.txtBuscar);


        cargarDibujos();

        findViewById(R.id.btnCerrar)
                .setOnClickListener(v -> finish());

        txtBuscar.addTextChangedListener(
                new TextWatcher() {




                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filtrar(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                }
        );



    }
    @Override
    protected void onResume() {
        super.onResume();

        cargarDibujos();
    }


    private void cargarDibujos() {

        dibujos.clear();
        dibujosOriginal.clear();

        Cursor cursor = db.obtenerDibujos();

        while (cursor.moveToNext()) {

            String ruta =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("rutaDibujo")
                    );

            File archivo = new File(ruta);

            if (archivo.exists()) {

                dibujos.add(ruta);
                dibujosOriginal.add(ruta);

            } else {

                db.eliminarDibujo(ruta);
            }
        }

        cursor.close();

        if (adaptador == null) {

            adaptador =
                    new AdaptadorDibujos(
                            this,
                            dibujos
                    );

            gridDibujos.setAdapter(adaptador);

        } else {

            adaptador.notifyDataSetChanged();
        }
    }

    private void filtrar(String texto) {

        dibujos.clear();

        if (texto == null || texto.trim().isEmpty()) {

            dibujos.addAll(dibujosOriginal);

        } else {

            String filtro = texto.toLowerCase();

            for (String ruta : dibujosOriginal) {

                File archivo = new File(ruta);

                String nombre = archivo.getName().toLowerCase();

                if (nombre.contains(filtro)) {
                    dibujos.add(ruta);
                }
            }
        }

        adaptador.notifyDataSetChanged();
    }
}