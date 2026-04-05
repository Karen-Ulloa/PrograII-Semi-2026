package com.example.miprimeraapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    DB db;
    Button btnGuardar;
    TextView tempVal;
    String accion = "nuevo", idProducto = "", urlFoto;
    FloatingActionButton fabLista;
    ImageView imgFoto;

    private static final int PERMISSIONS_CODE = 100;
    private static final int CAMERA_CODE = 1;
    private static final int GALERIA_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);

        imgFoto = findViewById(R.id.imgFotoAmigo);
        imgFoto.setOnClickListener(v -> mostrarOpcionesFoto());

        btnGuardar = findViewById(R.id.btnGuardarAmigo);
        btnGuardar.setOnClickListener(v -> guardarProductoConValidacion());

        fabLista = findViewById(R.id.fabListaAmigo);
        fabLista.setOnClickListener(v -> regresarLista());

        pedirPermisos();
        mostrarDatosProducto();
    }

    private void pedirPermisos() {
        String[] permisos = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean faltan = false;
            for (String p : permisos) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    faltan = true;
                    break;
                }
            }
            if (faltan) requestPermissions(permisos, PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CODE) {
            boolean todosAceptados = true;
            for (int r : grantResults) if (r != PackageManager.PERMISSION_GRANTED) todosAceptados = false;
            if (!todosAceptados)
                Toast.makeText(this, "Permisos requeridos para usar la cámara y galería", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDatosProducto() {
        try {
            Bundle parametros = getIntent().getExtras();
            if (parametros != null) {
                accion = parametros.getString("accion", "nuevo");
                if (accion.equals("modificar")) {
                    JSONObject datos = new JSONObject(parametros.getString("amigos"));
                    idProducto = datos.getString("idAmigo");

                    tempVal = findViewById(R.id.txtcodigoAmigos);
                    tempVal.setText(datos.getString("codigo"));

                    tempVal = findViewById(R.id.txtdescripcionAmigos);
                    tempVal.setText(datos.getString("descripcion"));

                    tempVal = findViewById(R.id.txtmarcaAmigos);
                    tempVal.setText(datos.getString("marca"));

                    tempVal = findViewById(R.id.txtpresentacionAmigos);
                    tempVal.setText(datos.getString("presentacion"));

                    tempVal = findViewById(R.id.txtprecioAmigos);
                    tempVal.setText(datos.getString("precio"));

                    urlFoto = datos.getString("foto");
                    if (!urlFoto.isEmpty()) imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al mostrar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 📸 Opciones foto: Cámara o Galería
    private void mostrarOpcionesFoto() {
        String[] opciones = {"Tomar foto", "Elegir de galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) abrirCamara();
            if (which == 1) abrirGaleria();
        });
        builder.show();
    }

    private void abrirCamara() {
        Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoArchivo = null;

        try {
            fotoArchivo = crearArchivoFoto();
            if (fotoArchivo != null) {
                Uri uriFoto = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        fotoArchivo
                );
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(tomarFotoIntent, CAMERA_CODE);
            } else {
                Toast.makeText(this, "No se pudo crear archivo foto", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error abrir cámara: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File crearArchivoFoto() throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = "foto_" + timestamp;
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (!dirAlmacenamiento.exists()) dirAlmacenamiento.mkdir();
        File imagen = File.createTempFile(nombreArchivo, ".jpg", dirAlmacenamiento);
        urlFoto = imagen.getAbsolutePath();
        return imagen;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALERIA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                imgFoto.setImageBitmap(BitmapFactory.decodeFile(urlFoto));
            } else if (requestCode == GALERIA_CODE) {
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    imgFoto.setImageURI(selectedImage);
                    urlFoto = getRealPathFromURI(selectedImage);
                }
            }
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_LONG).show();
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String path = "";
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return path;
    }

    // 🔹 Guardar producto con validación
    private void guardarProductoConValidacion() {
        tempVal = findViewById(R.id.txtcodigoAmigos);
        String codigo = tempVal.getText().toString().trim();

        tempVal = findViewById(R.id.txtdescripcionAmigos);
        String descripcion = tempVal.getText().toString().trim();

        tempVal = findViewById(R.id.txtmarcaAmigos);
        String marca = tempVal.getText().toString().trim();

        tempVal = findViewById(R.id.txtpresentacionAmigos);
        String presentacion = tempVal.getText().toString().trim();

        tempVal = findViewById(R.id.txtprecioAmigos);
        String precio = tempVal.getText().toString().trim();

        // ✅ Validación de campos
        if (codigo.isEmpty()) {
            tempVal.requestFocus();
            tempVal.setError("Ingrese el código");
            return;
        }
        if (descripcion.isEmpty()) {
            tempVal = findViewById(R.id.txtdescripcionAmigos);
            tempVal.requestFocus();
            tempVal.setError("Ingrese la descripción");
            return;
        }
        if (marca.isEmpty()) {
            tempVal = findViewById(R.id.txtmarcaAmigos);
            tempVal.requestFocus();
            tempVal.setError("Ingrese la marca");
            return;
        }
        if (presentacion.isEmpty()) {
            tempVal = findViewById(R.id.txtpresentacionAmigos);
            tempVal.requestFocus();
            tempVal.setError("Ingrese la presentación");
            return;
        }
        if (precio.isEmpty()) {
            tempVal = findViewById(R.id.txtprecioAmigos);
            tempVal.requestFocus();
            tempVal.setError("Ingrese el precio");
            return;
        }
        if (urlFoto == null || urlFoto.isEmpty()) {
            Toast.makeText(this, "Seleccione una imagen para el producto", Toast.LENGTH_LONG).show();
            return;
        }

        String[] datos = {idProducto, codigo, descripcion, marca, presentacion, precio, urlFoto};
        db.administrar_amigos(accion, datos);
        Toast.makeText(this, "Producto guardado con éxito", Toast.LENGTH_LONG).show();

        regresarLista();
    }

    private void regresarLista() {
        Intent intent = new Intent(this, lista_producto.class);
        startActivity(intent);
        finish();
    }
}