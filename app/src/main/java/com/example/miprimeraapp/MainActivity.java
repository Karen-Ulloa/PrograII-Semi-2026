package com.example.miprimeraapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    DB db;
    Button btnGuardar;
    ImageView imgFoto;
    com.google.android.material.floatingactionbutton.FloatingActionButton fab;
    EditText txtCodigo, txtDescripcion, txtMarca, txtPresentacion, txtCosto, txtPrecio, txtStock;
    TextView lblGananciaPreview;
    String accion     = "nuevo";
    String idProducto = "";
    String id         = "";
    String rev        = "";
    String urlFoto    = "";
    ArrayList<String> fotosTomadas = new ArrayList<>();
    final int CAMERA_CODE  = 1;
    final int GALERIA_CODE = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
            }
        }

        db = new DB(this);

        txtCodigo        = findViewById(R.id.txtCodigo);
        txtDescripcion   = findViewById(R.id.txtDescripcion);
        txtMarca         = findViewById(R.id.txtMarca);
        txtPresentacion  = findViewById(R.id.txtPresentacion);
        txtCosto         = findViewById(R.id.txtCosto);
        txtPrecio        = findViewById(R.id.txtPrecio);
        txtStock         = findViewById(R.id.txtStock);
        lblGananciaPreview = findViewById(R.id.lblGananciaPreview);
        imgFoto    = findViewById(R.id.imgFotoProducto);
        btnGuardar = findViewById(R.id.btnGuardarProducto);
        fab        = findViewById(R.id.fabListaProducto);

        imgFoto.setOnClickListener(v -> menuImagenes());
        btnGuardar.setOnClickListener(v -> guardarProducto());
        fab.setOnClickListener(v -> regresarLista());

        TextWatcher calcularGanancia = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { actualizarGananciaPreview(); }
        };

        txtCosto.addTextChangedListener(calcularGanancia);
        txtPrecio.addTextChangedListener(calcularGanancia);

        mostrarDatos();
    }

    private void actualizarGananciaPreview() {
        try {
            String sCosto  = txtCosto.getText().toString().trim();
            String sPrecio = txtPrecio.getText().toString().trim();
            if (!sCosto.isEmpty() && !sPrecio.isEmpty()) {
                double costo  = Double.parseDouble(sCosto);
                double precio = Double.parseDouble(sPrecio);
                if (costo > 0) {
                    double ganancia = ((precio - costo) / costo) * 100;
                    lblGananciaPreview.setText(String.format("Ganancia: %.1f%%", ganancia));
                } else {
                    lblGananciaPreview.setText("Ganancia: 0%");
                }
            } else {
                lblGananciaPreview.setText("Ganancia: -");
            }
        } catch (Exception e) {
            lblGananciaPreview.setText("Ganancia: -");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarDatos();
    }

    private void guardarProducto() {
        try {
            String codigo       = txtCodigo.getText().toString().trim();
            String descripcion  = txtDescripcion.getText().toString().trim();
            String marca        = txtMarca.getText().toString().trim();
            String presentacion = txtPresentacion.getText().toString().trim();
            String sCosto       = txtCosto.getText().toString().trim();
            String sPrecio      = txtPrecio.getText().toString().trim();
            String sStock       = txtStock.getText().toString().trim();

            if (sCosto.isEmpty() || sPrecio.isEmpty() || sStock.isEmpty()) {
                mostrarMsg("Debes completar Costo, Precio y Stock");
                return;
            }

            double costo    = Double.parseDouble(sCosto);
            double precio   = Double.parseDouble(sPrecio);
            double ganancia = (costo > 0) ? ((precio - costo) / costo) * 100 : 0;

            detectarInternet di = new detectarInternet(this);

            if (idProducto == null || idProducto.isEmpty()) {
                idProducto = String.valueOf(System.currentTimeMillis());
            }

            String[] datos = {
                    idProducto, codigo, descripcion, marca, presentacion,
                    String.valueOf(costo), String.valueOf(precio), sStock, urlFoto
            };

            if (!di.hayConexionInternet()) {
                String res = db.administrar_productos(accion, datos);
                if (res.equals("ok")) {
                    db.marcarPendienteSyncPorIdProducto(idProducto);
                    mostrarMsg("Sin conexión. Se sincronizará al deslizar la lista.");
                    regresarLista();
                } else {
                    mostrarMsg("Error SQLite: " + res);
                }

            } else {
                JSONObject json = new JSONObject();
                if (accion.equals("modificar")) {
                    json.put("_id",  id);
                    json.put("_rev", rev);
                }
                json.put("idProducto",   idProducto);
                json.put("codigo",       codigo);
                json.put("descripcion",  descripcion);
                json.put("marca",        marca);
                json.put("presentacion", presentacion);
                json.put("costo",        costo);
                json.put("precio",       precio);
                json.put("ganancia",     ganancia);
                json.put("stock",        sStock);
                json.put("foto",         urlFoto);

                EnviarDatosServidor enviar = new EnviarDatosServidor(this);
                enviar.execute(json.toString(), "POST", utilidades.url_mto);

                mostrarMsg("Producto guardado en servidor");
                regresarLista();

                sincronizarPendientes();
            }

        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }

    private void sincronizarPendientes() {
        new Thread(() -> {
            try {
                List<String[]> pendientes = db.obtenerPendientesSync();
                if (pendientes == null || pendientes.isEmpty()) return;

                int sincronizados = 0;
                for (String[] prod : pendientes) {
                    try {
                        double costoP    = Double.parseDouble(prod[5]);
                        double precioP   = Double.parseDouble(prod[6]);
                        double gananciaP = (costoP > 0) ? ((precioP - costoP) / costoP) * 100 : 0;

                        JSONObject json = new JSONObject();
                        json.put("idProducto",   prod[0]);
                        json.put("codigo",       prod[1]);
                        json.put("descripcion",  prod[2]);
                        json.put("marca",        prod[3]);
                        json.put("presentacion", prod[4]);
                        json.put("costo",        costoP);
                        json.put("precio",       precioP);
                        json.put("ganancia",     gananciaP);
                        json.put("stock",        prod[7]);
                        json.put("foto",         prod[8]);

                        boolean exito = enviarACouchDB(json.toString());
                        if (exito) {
                            db.eliminarPendienteSync(prod[0]);
                            sincronizados++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                final int total = sincronizados;
                if (total > 0) {
                    runOnUiThread(() ->
                            mostrarMsg("✅ " + total + " producto(s) sincronizado(s) con el servidor.")
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean enviarACouchDB(String jsonStr) {
        try {
            URL url = new URL(utilidades.url_mto);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonStr.getBytes("UTF-8"));
            }

            int code = conn.getResponseCode();
            conn.disconnect();
            return (code == 200 || code == 201);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private void menuImagenes() {
        String[] opciones = {"Tomar nueva foto", "Elegir de galería", "Escoger foto tomada"};
        new AlertDialog.Builder(this)
                .setTitle("Imagen del Producto")
                .setItems(opciones, (d, which) -> {
                    if (which == 0) tomarFoto();
                    if (which == 1) abrirGaleria();
                    if (which == 2) elegirFotoTomada();
                }).show();
    }

    private void tomarFoto() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File archivo = crearArchivoFoto();
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_CODE);
        } catch (Exception e) {
            mostrarMsg("Error al abrir cámara: " + e.getMessage());
        }
    }

    private File crearArchivoFoto() throws Exception {
        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File carpeta = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (carpeta != null && !carpeta.exists()) carpeta.mkdirs();
        File archivo = File.createTempFile("IMG_" + fecha, ".jpg", carpeta);
        urlFoto = archivo.getAbsolutePath();
        return archivo;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, GALERIA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                fotosTomadas.add(urlFoto);
                imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
            } else if (requestCode == GALERIA_CODE && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    urlFoto = guardarImagenGaleria(uri);
                    imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                }
            }
        }
    }

    private String guardarImagenGaleria(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            File archivo = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "GAL_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream output = new FileOutputStream(archivo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output);
            output.close();
            return archivo.getAbsolutePath();
        } catch (Exception e) { return ""; }
    }

    private void elegirFotoTomada() {
        if (fotosTomadas.isEmpty()) { mostrarMsg("No hay fotos previas"); return; }
        String[] lista = new String[fotosTomadas.size()];
        for (int i = 0; i < fotosTomadas.size(); i++) lista[i] = "Foto " + (i + 1);
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar")
                .setItems(lista, (d, which) -> {
                    urlFoto = fotosTomadas.get(which);
                    imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                }).show();
    }

    private void mostrarDatos() {
        try {
            Bundle b = getIntent().getExtras();
            if (b == null) return;
            accion = b.getString("accion", "nuevo");
            if (accion.equals("modificar")) {
                JSONObject datos = new JSONObject(b.getString("producto"));
                id          = datos.optString("_id",         "");
                rev         = datos.optString("_rev",        "");
                idProducto  = datos.optString("idProducto",  "");
                txtCodigo.setText(datos.optString("codigo",       ""));
                txtDescripcion.setText(datos.optString("descripcion",  ""));
                txtMarca.setText(datos.optString("marca",        ""));
                txtPresentacion.setText(datos.optString("presentacion", ""));
                txtCosto.setText(datos.optString("costo",        ""));
                txtPrecio.setText(datos.optString("precio",       ""));
                txtStock.setText(datos.optString("stock",        ""));
                urlFoto = datos.optString("foto", "");
                File archivo = new File(urlFoto);
                if (archivo.exists()) imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                actualizarGananciaPreview();
            }
        } catch (Exception e) { mostrarMsg("Error al cargar datos"); }
    }

    private void regresarLista() {
        startActivity(new Intent(this, lista_producto.class));
        finish();
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}