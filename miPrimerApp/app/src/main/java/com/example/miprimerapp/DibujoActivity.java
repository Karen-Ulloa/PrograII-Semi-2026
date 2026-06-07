package com.example.miprimerapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;



public class DibujoActivity extends Activity {

    Lienzo lienzo;

    LinearLayout btnGuardar,
            btnLimpiar,
            btnSalir,
            btnVerDibujos;

    TextView txtDibujos,
            txtArtista;

    DB db;

    int cantidadDibujos = 0;

    boolean guardando = false;
    String rutaEditar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dibujo);

        db = new DB(this);

        FrameLayout contenedor =
                findViewById(R.id.contenedorLienzo);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnSalir = findViewById(R.id.btnSalir);
        btnVerDibujos = findViewById(R.id.btnVerDibujos);

        txtDibujos = findViewById(R.id.txtDibujos);
        txtArtista = findViewById(R.id.txtArtista);

        lienzo = new Lienzo(this);
        contenedor.addView(lienzo);


        rutaEditar =
                getIntent().getStringExtra("rutaEditar");

        if (rutaEditar != null) {

            Bitmap bitmap =
                    android.graphics.BitmapFactory
                            .decodeFile(rutaEditar);

            if (bitmap != null) {

                lienzo.cargarImagen(bitmap);
            }
        }

        actualizarDatos();
        btnLimpiar.setOnClickListener(v -> {

            lienzo.deshacer();

            Toast.makeText(
                    this,
                    "Último trazo eliminado \uD83E\uDDF9",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnSalir.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            if (!guardando) guardarDibujo();
        });

        btnVerDibujos.setOnClickListener(v -> {
            startActivity(new Intent(this, ListaDibujosActivity.class));
        });

        findViewById(R.id.colorRojo)
                .setOnClickListener(v -> lienzo.cambiarColor(Color.parseColor("#F44336")));

        findViewById(R.id.colorNaranja)
                .setOnClickListener(v -> lienzo.cambiarColor(Color.parseColor("#FF9800")));

        findViewById(R.id.colorAmarillo)
                .setOnClickListener(v -> lienzo.cambiarColor(Color.parseColor("#FFD600")));

        findViewById(R.id.colorVerde)
                .setOnClickListener(v -> lienzo.cambiarColor(Color.parseColor("#4CAF50")));

        findViewById(R.id.colorMorado)
                .setOnClickListener(v -> lienzo.cambiarColor(Color.parseColor("#7C4DFF")));
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatos();
    }

    private void actualizarDatos() {

        Cursor cursor = db.obtenerDibujos();

        cantidadDibujos = cursor.getCount();

        cursor.close();

        txtDibujos.setText("♥ " + cantidadDibujos + " dibujos");

        actualizarNivel();
    }


    private void guardarDibujo() {

        try {

            if (!lienzo.hayDibujo()) {

                Toast.makeText(
                        this,
                        "Dibuja algo primero 🎨",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            guardando = true;

            Bitmap bitmap = Bitmap.createBitmap(
                    lienzo.getWidth(),
                    lienzo.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            android.graphics.Canvas canvas =
                    new android.graphics.Canvas(bitmap);

            lienzo.draw(canvas);

            File carpeta =
                    getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES
                    );

            if (carpeta == null) {

                guardando = false;
                return;
            }

            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            File archivo;

            // EDITAR
            if (rutaEditar != null) {

                archivo = new File(rutaEditar);

            }
            // NUEVO
            else {

                archivo = new File(
                        carpeta,
                        "dibujo_" +
                                System.currentTimeMillis()
                                + ".png"
                );
            }

            FileOutputStream output =
                    new FileOutputStream(archivo);

            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
            );

            output.flush();
            output.close();


            if (rutaEditar == null) {

                db.guardarDibujo(
                        archivo.getAbsolutePath()
                );
            }

            actualizarDatos();

            Toast.makeText(
                    this,
                    rutaEditar == null
                            ? "Guardado 🎨"
                            : "Dibujo actualizado ✏️",
                    Toast.LENGTH_SHORT
            ).show();

            guardando = false;

            finish();

        } catch (Exception e) {

            guardando = false;

            Toast.makeText(
                    this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void actualizarNivel() {

        if (cantidadDibujos >= 20) txtArtista.setText("👑 Leyenda");
        else if (cantidadDibujos >= 15) txtArtista.setText("🏆 Maestro");
        else if (cantidadDibujos >= 10) txtArtista.setText("⭐⭐ Pro");
        else if (cantidadDibujos >= 5) txtArtista.setText("⭐ Medio");
        else txtArtista.setText("★ Novato");
    }



}