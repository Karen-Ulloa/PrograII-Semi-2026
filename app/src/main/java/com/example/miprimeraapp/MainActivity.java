package com.example.miprimeraapp;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.miprimeraapp.EnviarDatosServidor;
import com.example.miprimeraapp.detectarInternet;
import com.example.miprimeraapp.lista_producto;
import com.example.miprimeraapp.utilidades;
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
    ImageView imgFoto;
    FloatingActionButton fab;

    String accion = "nuevo";
    String idProducto = "";
    String id = "";
    String rev = "";
    String urlFoto = "";

    ArrayList<String> fotosTomadas = new ArrayList<>();

    final int CAMERA_CODE = 1;
    final int GALERIA_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);

        imgFoto = findViewById(R.id.imgFotoAmigo);
        btnGuardar = findViewById(R.id.btnGuardarAmigo);
        fab = findViewById(R.id.fabListaAmigo);

        imgFoto.setOnClickListener(v -> menuImagenes());
        btnGuardar.setOnClickListener(v -> guardarProducto());
        fab.setOnClickListener(v -> regresarLista());

        mostrarDatos();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mostrarDatos();
    }


    private void menuImagenes() {

        String[] opciones = {
                "Tomar nueva foto",
                "Elegir de galería",
                "Escoger foto tomada"
        };

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Imagen");

        builder.setItems(opciones, (d, which) -> {

            if (which == 0) tomarFoto();
            if (which == 1) abrirGaleria();
            if (which == 2) elegirFotoTomada();

        });

        builder.show();
    }


    private void tomarFoto() {

        try {

            Intent intent =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File archivo = crearArchivoFoto();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
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

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );

        startActivityForResult(
                intent,
                GALERIA_CODE
        );
    }


    private void elegirFotoTomada() {

        if (fotosTomadas.size() == 0) {
            mostrarMsg("No hay fotos");
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

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Escoger");

        builder.setItems(lista, (d, which) -> {

            urlFoto =
                    fotosTomadas.get(which);

            imgFoto.setImageBitmap(
                    BitmapFactory.decodeFile(
                            urlFoto
                    )
            );
        });

        builder.show();
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

                if (requestCode ==
                        CAMERA_CODE) {

                    fotosTomadas.add(urlFoto);

                    imgFoto.setImageBitmap(
                            BitmapFactory.decodeFile(
                                    urlFoto
                            )
                    );
                }

                if (requestCode ==
                        GALERIA_CODE &&
                        data != null) {

                    Uri uri =
                            data.getData();

                    if (uri != null) {

                        getContentResolver()
                                .takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );

                        urlFoto =
                                guardarImagenGaleria(
                                        uri
                                );

                        imgFoto.setImageBitmap(
                                BitmapFactory.decodeFile(
                                        urlFoto
                                )
                        );
                    }
                }
            }

        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }

    private String guardarImagenGaleria(
            Uri uri
    ) {
        try {

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
                            "GAL_" +
                                    System.currentTimeMillis() +
                                    ".jpg"
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

            return archivo
                    .getAbsolutePath();

        } catch (Exception e) {
            return "";
        }
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

            if (accion.equals(
                    "modificar"
            )) {

                JSONObject datos =
                        new JSONObject(
                                b.getString(
                                        "producto"
                                )
                        );

                id =
                        datos.optString(
                                "_id",
                                ""
                        );

                rev =
                        datos.optString(
                                "_rev",
                                ""
                        );

                idProducto =
                        datos.optString(
                                "idProducto",
                                ""
                        );

                ((TextView)findViewById(
                        R.id.txtcodigoAmigos))
                        .setText(
                                datos.optString(
                                        "codigo",
                                        ""
                                ));

                ((TextView)findViewById(
                        R.id.txtdescripcionAmigos))
                        .setText(
                                datos.optString(
                                        "descripcion",
                                        ""
                                ));

                ((TextView)findViewById(
                        R.id.txtmarcaAmigos))
                        .setText(
                                datos.optString(
                                        "marca",
                                        ""
                                ));

                ((TextView)findViewById(
                        R.id.txtpresentacionAmigos))
                        .setText(
                                datos.optString(
                                        "presentacion",
                                        ""
                                ));

                ((TextView)findViewById(
                        R.id.txtprecioAmigos))
                        .setText(
                                datos.optString(
                                        "precio",
                                        ""
                                ));

                urlFoto =
                        datos.optString(
                                "foto",
                                ""
                        );

                File archivo =
                        new File(urlFoto);

                if (archivo.exists()) {

                    imgFoto.setImageBitmap(
                            BitmapFactory.decodeFile(
                                    urlFoto
                            )
                    );
                }
            }

        } catch (Exception e) {
            mostrarMsg("Error datos");
        }
    }


    private void guardarProducto() {

        try {

            String codigo =
                    ((TextView)findViewById(
                            R.id.txtcodigoAmigos))
                            .getText()
                            .toString()
                            .trim();

            String descripcion =
                    ((TextView)findViewById(
                            R.id.txtdescripcionAmigos))
                            .getText()
                            .toString()
                            .trim();

            String marca =
                    ((TextView)findViewById(
                            R.id.txtmarcaAmigos))
                            .getText()
                            .toString()
                            .trim();

            String presentacion =
                    ((TextView)findViewById(
                            R.id.txtpresentacionAmigos))
                            .getText()
                            .toString()
                            .trim();

            String precio =
                    ((TextView)findViewById(
                            R.id.txtprecioAmigos))
                            .getText()
                            .toString()
                            .trim();

            if (codigo.isEmpty() ||
                    descripcion.isEmpty() ||
                    marca.isEmpty() ||
                    presentacion.isEmpty() ||
                    precio.isEmpty()) {

                mostrarMsg(
                        "Complete campos"
                );
                return;
            }

            if (urlFoto.isEmpty()) {
                mostrarMsg(
                        "Seleccione imagen"
                );
                return;
            }

            detectarInternet di =
                    new detectarInternet(this);

            //==========================================
            // SIN INTERNET -> SQLITE
            //==========================================
            if (!di.hayConexionInternet()) {

                String[] datos = {
                        idProducto,
                        codigo,
                        descripcion,
                        marca,
                        presentacion,
                        precio,
                        urlFoto
                };

                db.administrar_amigos(
                        accion,
                        datos
                );

                mostrarMsg(
                        "Guardado en SQLite"
                );

                regresarLista();
                return;
            }

            //==========================================
            // CON INTERNET -> COUCHDB
            //==========================================
            JSONObject json =
                    new JSONObject();

            if (accion.equals(
                    "modificar"
            )) {

                json.put("_id", id);
                json.put("_rev", rev);
            }

            if (idProducto.equals("")) {

                idProducto =
                        String.valueOf(
                                System.currentTimeMillis()
                        );
            }

            json.put(
                    "idProducto",
                    idProducto
            );

            json.put(
                    "codigo",
                    codigo
            );

            json.put(
                    "descripcion",
                    descripcion
            );

            json.put(
                    "marca",
                    marca
            );

            json.put(
                    "presentacion",
                    presentacion
            );

            json.put(
                    "precio",
                    precio
            );

            json.put(
                    "foto",
                    urlFoto
            );

            EnviarDatosServidor enviar =
                    new EnviarDatosServidor(
                            this
                    );

            String respuesta =
                    enviar.execute(
                            json.toString(),
                            "POST",
                            utilidades.url_mto
                    ).get();

            JSONObject resp =
                    new JSONObject(
                            respuesta
                    );

            if (resp.has("id")) {
                id =
                        resp.getString(
                                "id"
                        );
            }

            if (resp.has("rev")) {
                rev =
                        resp.getString(
                                "rev"
                        );
            }

            mostrarMsg(
                    "Guardado en CouchDB"
            );

            regresarLista();

        } catch (Exception e) {

            mostrarMsg(
                    "Error: " +
                            e.getMessage()
            );
        }
    }

    //==================================================
    private void regresarLista() {

        Intent i =
                new Intent(
                        this,
                        lista_producto.class
                );

        startActivity(i);
        finish();
    }

    private void mostrarMsg(
            String msg
    ) {
        Toast.makeText(
                this,
                msg,
                Toast.LENGTH_LONG
        ).show();
    }
}