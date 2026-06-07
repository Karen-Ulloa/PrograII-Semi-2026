package com.example.miprimerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_producto extends Activity {

    DB db;
    FloatingActionButton fab;
    FloatingActionButton fabRecargar;
    ListView lista;

    Cursor cursor;

    ArrayList<producto> listaProductos = new ArrayList<>();
    ArrayList<producto> copiaProductos = new ArrayList<>();

    JSONArray jsonArray;

    int posicion = 0;

    detectarinternet di;
    obtenerDatosServidor datosServidor;

    Bundle parametros = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_producto);

        db = new DB(this);
        di = new detectarinternet(this);

        parametros.putString("accion", "nuevo");

        fab = findViewById(R.id.fabAgregarAmigos);
        fabRecargar = findViewById(R.id.fabRecargar);
        lista = findViewById(R.id.ltsAmigos);

        fab.setOnClickListener(v -> abrirFormulario());

        fabRecargar.setOnClickListener(v -> {
            mostrarMsg("Actualizando...");
            obtenerProductos();
        });

        // BOTONES NUEVOS
        LinearLayout btnEstadisticas = findViewById(R.id.btnEstadisticas);
        LinearLayout btnDibujo = findViewById(R.id.btnDibujo);

        // ABRIR ESTADISTICAS
        btnEstadisticas.setOnClickListener(v -> {
            Intent i = new Intent(lista_producto.this, EstadisticasActivity.class);
            startActivity(i);
        });

        // ABRIR DIBUJO
        btnDibujo.setOnClickListener(v -> {
            Intent i = new Intent(lista_producto.this, DibujoActivity.class);
            startActivity(i);
        });

        obtenerProductos();
        buscarProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerProductos();
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        try {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;

            posicion = info.position;

            JSONObject fila = jsonArray.getJSONObject(posicion);
            JSONObject value = fila.optJSONObject("value");

            if (value == null) value = fila;

            menu.setHeaderTitle(value.optString("descripcion", "Producto"));

        } catch (Exception e) {
            mostrarMsg("Error menú");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        try {

            JSONObject fila = jsonArray.getJSONObject(posicion);
            JSONObject value = fila.optJSONObject("value");

            if (value == null) value = fila;

            if (item.getItemId() == R.id.mnxAgregar) {

                parametros.putString("accion", "nuevo");
                abrirFormulario();

            } else if (item.getItemId() == R.id.mnxModificar) {

                parametros.putString("accion", "modificar");
                parametros.putString("producto", value.toString());
                abrirFormulario();

            } else if (item.getItemId() == R.id.mnxEliminar) {

                eliminarProducto(value);
            }

            return true;

        } catch (Exception e) {
            mostrarMsg("Error menú");
            return false;
        }
    }



    private void eliminarProducto(JSONObject value) {

        try {

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar producto")
                    .setMessage(value.optString("descripcion"))
                    .setPositiveButton("SI", (d, w) -> {

                        try {

                            String idProducto = value.optString("idProducto");
                            String id = value.optString("_id");
                            String rev = value.optString("_rev");


                            db.administrar_amigos(
                                    "eliminar",
                                    new String[]{idProducto}
                            );


                            JSONObject json = new JSONObject();
                            json.put("_id", id);
                            json.put("_rev", rev);
                            json.put("_deleted", true);

                            enviarDatosServidor enviar = new enviarDatosServidor(this);
                            enviar.execute(json.toString(), "POST", utilidades.url_mto);

                            mostrarMsg("Eliminado correctamente");
                            obtenerProductos();

                        } catch (Exception e) {
                            mostrarMsg("Error eliminar: " + e.getMessage());
                        }

                    })
                    .setNegativeButton("NO", null)
                    .show();

        } catch (Exception e) {
            mostrarMsg("Error");
        }
    }


    private void buscarProductos() {

        TextView buscar = findViewById(R.id.txtBuscarAmigos);

        buscar.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}

            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {

                String txt = s.toString().toLowerCase().trim();

                listaProductos.clear();

                if (txt.isEmpty()) {
                    listaProductos.addAll(copiaProductos);
                } else {

                    for (producto p : copiaProductos) {

                        if (p.getDescripcion().toLowerCase().contains(txt)
                                || p.getCodigo().toLowerCase().contains(txt)
                                || p.getMarca().toLowerCase().contains(txt)) {

                            listaProductos.add(p);
                        }
                    }
                }

                lista.setAdapter(new AdaptadorProducto(
                        lista_producto.this,
                        listaProductos
                ));
            }
        });
    }



    private void abrirFormulario() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtras(parametros);
        startActivity(i);
    }



    private void obtenerProductos() {

        try {

            jsonArray = new JSONArray();


            if (di.hayConexionInternet()) {

                try {

                    datosServidor = new obtenerDatosServidor();
                    String resp = datosServidor.execute().get();

                    if (resp != null && !resp.contains("error")) {
                        JSONObject jsonObject = new JSONObject(resp);
                        jsonArray = jsonObject.getJSONArray("rows");
                    } else {
                        cargarDesdeSQLite();
                    }

                } catch (Exception e) {

                    cargarDesdeSQLite();
                }

            } else {

                cargarDesdeSQLite();
            }

            mostrarProductos();

        } catch (Exception e) {
            mostrarMsg("Error cargar datos: " + e.getMessage());
        }
    }



    private void cargarDesdeSQLite() {

        try {

            cursor = db.lista_amigos();

            if (cursor.moveToFirst()) {

                do {

                    JSONObject fila = new JSONObject();
                    JSONObject value = new JSONObject();

                    value.put("idProducto", cursor.getString(1));
                    value.put("codigo", cursor.getString(2));
                    value.put("descripcion", cursor.getString(3));
                    value.put("marca", cursor.getString(4));
                    value.put("presentacion", cursor.getString(5));
                    value.put("precio", cursor.getString(6));
                    value.put("foto", cursor.getString(7));
                    value.put("costo", cursor.getString(8));
                    value.put("stock", cursor.getString(9));
                    value.put("ganancia", cursor.getString(10));

                    fila.put("value", value);
                    jsonArray.put(fila);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            mostrarMsg("Error SQLite: " + e.getMessage());
        }
    }


    private void mostrarProductos() {

        try {

            listaProductos.clear();
            copiaProductos.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject fila = jsonArray.getJSONObject(i);
                JSONObject value = fila.optJSONObject("value");

                if (value == null) value = fila;

                producto p = new producto(
                        value.optString("idProducto"),
                        value.optString("codigo"),
                        value.optString("descripcion"),
                        value.optString("marca"),
                        value.optString("presentacion"),
                        value.optString("precio"),
                        value.optString("foto"),
                        value.optString("costo"),
                        value.optString("stock"),
                        value.optString("ganancia"),
                        value.optString("emocion")
                );

                listaProductos.add(p);
            }

            copiaProductos.addAll(listaProductos);

            lista.setAdapter(new AdaptadorProducto(
                    this,
                    listaProductos
            ));

            registerForContextMenu(lista);

        } catch (Exception e) {
            mostrarMsg("Error mostrar lista");
        }
    }



    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}