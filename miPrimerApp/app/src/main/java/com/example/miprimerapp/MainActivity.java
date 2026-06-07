package com.example.miprimerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    DB db;

    Button btnGuardar;

    FloatingActionButton fabRegresar;

    ImageView imgFoto;

    EditText txtDescripcion,
            txtMarca,
            txtPresentacion,
            txtPrecio,
            txtCosto,
            txtStock,
            txtCodigo;

    String accion = "nuevo";

    String idProducto = "";
    String id = "";
    String rev = "";

    String urlFoto = "";

    String emocion = "";

    ArrayList<String> fotosTomadas =
            new ArrayList<>();

    final int CAMERA_CODE = 1;
    final int GALERIA_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        db = new DB(this);

        // CAMPOS
        txtCodigo =
                findViewById(R.id.txtcodigoAmigos);

        txtDescripcion =
                findViewById(R.id.txtdescripcionAmigos);

        txtMarca =
                findViewById(R.id.txtmarcaAmigos);

        txtPresentacion =
                findViewById(R.id.txtpresentacionAmigos);

        txtPrecio =
                findViewById(R.id.txtprecioAmigos);

        txtCosto =
                findViewById(R.id.txtcostoAmigos);

        txtStock =
                findViewById(R.id.txtstockAmigos);

        // BOTONES
        imgFoto =
                findViewById(R.id.imgFotoAmigo);

        btnGuardar =
                findViewById(R.id.btnGuardarAmigo);

        fabRegresar =
                findViewById(R.id.fabListaAmigo);

        fabRegresar.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            lista_producto.class
                    )
            );

            finish();
        });

        imgFoto.setOnClickListener(v ->
                menuImagenes());

        btnGuardar.setOnClickListener(v ->
                guardarProducto());

        mostrarDatos();

        // =========================
        // BOTONES DINAMICOS NIÑOS
        // =========================

        // NOMBRE
        txtDescripcion.setOnClickListener(v -> {

            String[] opciones = {
                    "Día Galaxia 🚀",
                    "El Día Chispita ✨",
                    "Día Florecer 🌸",
                    "Día Marchito 🍂",
                    "Día Tormenta ⛈️",
                    "Día Erupción 🌋",
                    "Día Agujero Negro ⬛"
            };

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle("Selecciona tu dia");

            builder.setItems(opciones,
                    (dialog, which) ->
                            txtDescripcion.setText(
                                    opciones[which]
                            ));

            builder.show();
        });

        // TIPO DE DIA
        txtMarca.setOnClickListener(v -> {

            String[] opciones = {
                    "Escuela 🏫",
                    "Juego 🎮",
                    "Paseo 🚗",
                    "Familia 👨‍👩‍👧",
                    "Comida 🍕",
                    "Deportes ⚽"
            };

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle("¿Qué hiciste hoy?");

            builder.setItems(opciones,
                    (dialog, which) ->
                            txtMarca.setText(
                                    opciones[which]
                            ));

            builder.show();
        });

        // ACTIVIDAD
        txtPresentacion.setOnClickListener(v -> {

            String[] opciones = {
                    "Jugué con mis amigos 😊",
                    "Fui a la escuela 📚",
                    "Comí algo rico 🍔",
                    "Vi televisión 📺",
                    "Salí al parque 🌳",
                    "Dormí mucho 😴"
            };

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle("Selecciona una actividad");

            builder.setItems(opciones,
                    (dialog, which) ->
                            txtPresentacion.setText(
                                    opciones[which]
                            ));

            builder.show();
        });

        // COSAS BUENAS
        txtPrecio.setOnClickListener(v -> {

            String[] opciones = {
                    "1",
                    "2",
                    "3",
                    "4",
                    "5"
            };

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle(
                    "¿Cuántas cosas buenas? ⭐"
            );

            builder.setItems(opciones,
                    (dialog, which) ->
                            txtPrecio.setText(
                                    opciones[which]
                            ));

            builder.show();
        });

        // DIFICULTADES
        txtCosto.setOnClickListener(v -> {

            String[] opciones = {
                    "0",
                    "1",
                    "2",
                    "3",
                    "4"
            };

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle(
                    "¿Tuviste dificultades? 🎯"
            );

            builder.setItems(opciones,
                    (dialog, which) ->
                            txtCosto.setText(
                                    opciones[which]
                            ));

            builder.show();
        });

        // VECES
        txtStock.setOnClickListener(v -> {

            String[] opciones = {
                    "1 vez",
                    "2 veces",
                    "3 veces",
                    "4 veces",
                    "5 veces"
            };

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle(
                    "¿Cuántas veces pasó? 🎈"
            );

            builder.setItems(opciones,
                    (dialog, which) ->
                            txtStock.setText(
                                    String.valueOf(
                                            which + 1
                                    )
                            ));

            builder.show();
        });
    }

    private String generarId() {

        return String.valueOf(
                System.currentTimeMillis()
        );
    }

    private void menuImagenes() {

        String[] opciones = {
                "Tomar foto 📸",
                "Elegir de galería 🖼",
                "Fotos guardadas 📁"
        };

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle(
                "Elige una imagen divertida 😊"
        );

        builder.setItems(opciones,
                (d, which) -> {

                    if (which == 0)
                        tomarFoto();

                    else if (which == 1)
                        abrirGaleria();

                    else if (which == 2)
                        elegirFotoTomada();
                });

        builder.show();
    }

    private void tomarFoto() {

        try {

            Intent intent =
                    new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                    );

            File archivo =
                    crearArchivoFoto();

            Uri uri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            archivo
                    );

            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    uri
            );

            startActivityForResult(
                    intent,
                    CAMERA_CODE
            );

        } catch (Exception e) {

            mostrarMsg(e.getMessage());
        }
    }

    private File crearArchivoFoto()
            throws Exception {

        String fecha =
                new SimpleDateFormat(
                        "yyyyMMdd_HHmmss"
                ).format(new Date());

        File carpeta =
                getExternalFilesDir(
                        Environment.DIRECTORY_DCIM
                );

        if (carpeta != null &&
                !carpeta.exists()) {

            carpeta.mkdirs();
        }

        File archivo =
                File.createTempFile(
                        "IMG_" + fecha,
                        ".jpg",
                        carpeta
                );

        urlFoto =
                archivo.getAbsolutePath();

        return archivo;
    }

    private void abrirGaleria() {

        Intent intent =
                new Intent(
                        Intent.ACTION_OPEN_DOCUMENT
                );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("image/*");

        startActivityForResult(
                intent,
                GALERIA_CODE
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        try {

            if (resultCode == RESULT_OK) {

                if (requestCode == CAMERA_CODE) {

                    fotosTomadas.add(urlFoto);

                    imgFoto.setImageBitmap(
                            BitmapFactory.decodeFile(
                                    urlFoto
                            )
                    );
                }

                if (requestCode == GALERIA_CODE
                        && data != null) {

                    Uri uri = data.getData();

                    InputStream input =
                            getContentResolver()
                                    .openInputStream(uri);

                    Bitmap bitmap =
                            BitmapFactory.decodeStream(
                                    input
                            );

                    if (input != null)
                        input.close();

                    File archivo =
                            new File(
                                    getExternalFilesDir(
                                            Environment.DIRECTORY_DCIM
                                    ),
                                    "GAL_"
                                            + System.currentTimeMillis()
                                            + ".jpg"
                            );

                    FileOutputStream output =
                            new FileOutputStream(
                                    archivo
                            );

                    bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            85,
                            output
                    );

                    output.flush();
                    output.close();

                    urlFoto =
                            archivo.getAbsolutePath();

                    imgFoto.setImageBitmap(
                            BitmapFactory.decodeFile(
                                    urlFoto
                            )
                    );
                }
            }

        } catch (Exception e) {

            mostrarMsg(e.getMessage());
        }
    }

    private void guardarProducto() {
        try {
            String codigo = txtCodigo.getText().toString().trim();
            String descripcion = txtDescripcion.getText().toString().trim();
            String marca = txtMarca.getText().toString().trim();
            String presentacion = txtPresentacion.getText().toString().trim();
            String precioStr = txtPrecio.getText().toString().trim();
            String costoStr = txtCosto.getText().toString().trim();
            String stockStr = txtStock.getText().toString().trim();

            if (descripcion.isEmpty()
                    || marca.isEmpty()
                    || presentacion.isEmpty()
                    || precioStr.isEmpty()
                    || costoStr.isEmpty()
                    || stockStr.isEmpty()) {

                mostrarMsg("Complete todos los campos");
                return;
            }

            if (urlFoto == null || urlFoto.isEmpty()) {
                mostrarMsg("Selecciona una imagen 📸");
                return;
            }

            double precio = Double.parseDouble(precioStr);
            double costo = Double.parseDouble(costoStr);
            int stock = Integer.parseInt(stockStr);

            double ganancia;

            // CORRECCIÓN 1: Manejo de Costo 0 (Día sin dificultades)
            if (costo > 0) {
                ganancia = ((precio - costo) / costo) * 100;
            } else if (precio > 0) {
                // Si tuvo cosas buenas (precio) y 0 dificultades (costo), es un día excelente
                ganancia = 100;
            } else {
                ganancia = 0;
            }

            // ASIGNAR EMOCION
            if (ganancia >= 50) {
                emocion = "😄";
            } else if (ganancia >= 20) {
                emocion = "😐";
            } else {
                emocion = "😢";
            }

            if (idProducto.isEmpty()) {
                idProducto = generarId();
            }

            String[] datos = {
                    idProducto,
                    codigo,
                    descripcion,
                    marca,
                    presentacion,
                    String.valueOf(precio),
                    urlFoto,
                    String.valueOf(costo),
                    String.valueOf(stock),
                    String.valueOf(ganancia),
                    emocion
            };

            // CORRECCIÓN 2: Guardar en la base de datos local del teléfono SIEMPRE
            db.administrar_amigos(accion, datos);

            // Preparamos el objeto JSON por si hay que enviarlo a la nube
            JSONObject json = new JSONObject();
            json.put("idProducto", idProducto);
            json.put("codigo", codigo);
            json.put("descripcion", descripcion);
            json.put("marca", marca);
            json.put("presentacion", presentacion);
            json.put("precio", precio);
            json.put("foto", urlFoto);
            json.put("costo", costo);
            json.put("stock", stock);
            json.put("ganancia", ganancia);

            if (accion.equals("modificar")) {
                json.put("_id", id);
                json.put("_rev", rev);
            }

            detectarinternet di = new detectarinternet(this);

            if (!di.hayConexionInternet()) {
                // Si no hay internet, el flujo termina aquí ya habiendo guardado localmente
                mostrarMsg("¡Guardado en el dispositivo! 🎉");
                regresarLista();
                return;
            }

            // Si SÍ hay internet, además de haber guardado en el teléfono, lo sube al servidor
            enviarDatosServidor enviar = new enviarDatosServidor(this);
            enviar.execute(
                    json.toString(),
                    "POST",
                    utilidades.url_mto
            );

            mostrarMsg("¡Guardado y Sincronizado en la nube! 🚀");
            regresarLista();

        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }

    private void elegirFotoTomada() {

        if (fotosTomadas.isEmpty()) {

            mostrarMsg("No hay fotos 😢");

            return;
        }

        String[] lista =
                new String[fotosTomadas.size()];

        for (int i = 0;
             i < fotosTomadas.size();
             i++) {

            lista[i] =
                    "Foto " + (i + 1);
        }

        new AlertDialog.Builder(this)

                .setTitle(
                        "Escoge una foto 📁"
                )

                .setItems(lista,
                        (d, which) -> {

                            urlFoto =
                                    fotosTomadas.get(which);

                            imgFoto.setImageBitmap(
                                    BitmapFactory.decodeFile(
                                            urlFoto
                                    )
                            );
                        })

                .show();
    }

    private void mostrarDatos() {

        try {

            Bundle b =
                    getIntent().getExtras();

            if (b == null)
                return;

            accion =
                    b.getString(
                            "accion",
                            "nuevo"
                    );

            if (!accion.equals("modificar"))
                return;

            JSONObject datos =
                    new JSONObject(
                            b.getString(
                                    "producto",
                                    "{}"
                            )
                    );

            id =
                    datos.optString("_id");

            rev =
                    datos.optString("_rev");

            idProducto =
                    datos.optString("idProducto");

            txtCodigo.setText(
                    datos.optString("codigo")
            );

            txtDescripcion.setText(
                    datos.optString("descripcion")
            );

            txtMarca.setText(
                    datos.optString("marca")
            );

            txtPresentacion.setText(
                    datos.optString("presentacion")
            );

            txtPrecio.setText(
                    datos.optString("precio")
            );

            txtCosto.setText(
                    datos.optString("costo")
            );

            txtStock.setText(
                    datos.optString("stock")
            );

            urlFoto =
                    datos.optString("foto");

            if (!urlFoto.isEmpty()) {

                imgFoto.setImageBitmap(
                        BitmapFactory.decodeFile(
                                urlFoto
                        )
                );
            }

        } catch (Exception e) {

            mostrarMsg(
                    "Error al cargar datos"
            );
        }
    }

    private void regresarLista() {

        startActivity(
                new Intent(
                        this,
                        lista_producto.class
                )
        );

        finish();
    }

    private void mostrarMsg(String msg) {

        Toast.makeText(
                this,
                msg,
                Toast.LENGTH_LONG
        ).show();
    }
}