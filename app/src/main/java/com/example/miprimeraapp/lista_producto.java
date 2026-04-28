package com.example.miprimeraapp;

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

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class lista_producto extends Activity {

    DB db;
    FloatingActionButton fab;
    ListView lista;
    SwipeRefreshLayout swipeRefresh; // ← NUEVO
    Cursor cursor;

    ArrayList<producto> listaProductos = new ArrayList<>();
    ArrayList<producto> copiaProductos = new ArrayList<>();

    JSONArray jsonArray;
    JSONObject jsonObject;

    int posicion = 0;

    detectarInternet di;
    obtenerDatosServidor datosServidor;

    Bundle parametros = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_producto);

        db  = new DB(this);
        di  = new detectarInternet(this);

        parametros.putString("accion", "nuevo");

        fab = findViewById(R.id.fabAgregarProductos);
        fab.setOnClickListener(v -> abrirFormulario());

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(
                android.graphics.Color.parseColor("#1565C0"),
                android.graphics.Color.parseColor("#42A5F5")
        );

        swipeRefresh.setOnRefreshListener(() -> {
            if (di.hayConexionInternet()) {
                sincronizarPendientesYrecargar();
            } else {
                mostrarMsg("Sin conexión, mostrando datos locales");
                obtenerProductos();
                swipeRefresh.setRefreshing(false);
            }
        });

        obtenerProductos();
        buscarProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerProductos();
    }

    private void sincronizarPendientesYrecargar() {
        new Thread(() -> {
            try {
                List<String[]> pendientes = db.obtenerPendientesSync();

                for (String[] prod : pendientes) {
                    try {
                        double costoP    = Double.parseDouble(prod[5]);
                        double precioP   = Double.parseDouble(prod[6]);
                        double gananciaP = 0;
                        if (costoP > 0) gananciaP = ((precioP - costoP) / costoP) * 100;

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
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> {
                    int cantidad = pendientes.size();
                    if (cantidad > 0) {
                        mostrarMsg(" ✔ " + cantidad + " producto(s) sincronizado(s)");
                    }
                    obtenerProductos();
                    swipeRefresh.setRefreshing(false); // ← detiene el spinner
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    mostrarMsg("Error al sincronizar: " + e.getMessage());
                    swipeRefresh.setRefreshing(false);
                });
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
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            JSONObject fila  = jsonArray.getJSONObject(posicion);
            JSONObject value = fila.optJSONObject("doc");
            if (value == null) value = fila.optJSONObject("value");
            if (value == null) value = fila;
            menu.setHeaderTitle(value.optString("descripcion", "Producto"));
        } catch (Exception e) {
            mostrarMsg("Error menú");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            JSONObject fila  = jsonArray.getJSONObject(posicion);
            JSONObject value = fila.optJSONObject("doc");
            if (value == null) value = fila.optJSONObject("value");
            if (value == null) value = fila;

            if (item.getItemId() == R.id.mnxAgregar) {
                parametros.clear();
                parametros.putString("accion", "nuevo");
                abrirFormulario();
            } else if (item.getItemId() == R.id.mnxModificar) {
                parametros.clear();
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("¿Eliminar este producto?");
            dialog.setMessage(value.optString("descripcion"));

            dialog.setPositiveButton("SI", (d, w) -> {
                try {
                    String idProducto = value.optString("idProducto");
                    db.administrar_productos("eliminar", new String[]{idProducto});

                    if (di.hayConexionInternet()) {
                        String _id  = value.optString("_id");
                        String _rev = value.optString("_rev");
                        if (!_id.isEmpty() && !_rev.isEmpty()) {
                            String url = utilidades.url_mto + "/" + _id + "?rev=" + _rev;
                            EnviarDatosServidor enviar = new EnviarDatosServidor(this);
                            enviar.execute("{}", "DELETE", url);
                        }
                    }

                    mostrarMsg("Producto eliminado correctamente");
                    obtenerProductos();
                } catch (Exception e) {
                    mostrarMsg("Error al eliminar");
                }
            });

            dialog.setNegativeButton("NO", (d, w) -> d.dismiss());
            dialog.show();
        } catch (Exception e) {
            mostrarMsg("Error");
        }
    }

    private void buscarProductos() {
        TextView buscar = findViewById(R.id.txtBuscarProductos);
        buscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                listaProductos.clear();
                String txt = buscar.getText().toString().toLowerCase().trim();
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
                lista.setAdapter(new AdaptadorProducto(lista_producto.this, listaProductos));
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
                datosServidor = new obtenerDatosServidor();
                String resp = datosServidor.execute().get();
                jsonObject  = new JSONObject(resp);
                jsonArray   = jsonObject.getJSONArray("rows");
            } else {
                cursor = db.lista_productos();
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject fila  = new JSONObject();
                        JSONObject value = new JSONObject();
                        value.put("idProducto",  cursor.getString(0));
                        value.put("codigo",      cursor.getString(1));
                        value.put("descripcion", cursor.getString(2));
                        value.put("marca",       cursor.getString(3));
                        value.put("presentacion",cursor.getString(4));
                        value.put("costo",       cursor.getDouble(5));
                        value.put("precio",      cursor.getDouble(6));
                        value.put("ganancia",    cursor.getDouble(7));
                        value.put("stock",       cursor.getInt(8));
                        value.put("foto",        cursor.getString(9));
                        fila.put("value", value);
                        jsonArray.put(fila);
                    } while (cursor.moveToNext());
                }
            }
            mostrarProductos();

        } catch (Exception e) {
            mostrarMsg("Error al cargar productos: " + e.getMessage());
        }
    }

    private void mostrarProductos() {
        try {
            lista = findViewById(R.id.ltsProductos);
            listaProductos.clear();
            copiaProductos.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject fila  = jsonArray.getJSONObject(i);
                JSONObject value = fila.optJSONObject("doc");
                if (value == null) value = fila.optJSONObject("value");
                if (value == null) continue;

                String id = value.optString("_id", "");
                if (id.startsWith("_design")) continue;

                producto p = new producto(
                        value.optString("idProducto"),
                        value.optString("codigo"),
                        value.optString("descripcion"),
                        value.optString("marca"),
                        value.optString("presentacion"),
                        value.optDouble("costo", 0),
                        value.optDouble("precio", 0),
                        value.optInt("stock", 0),
                        value.optString("foto")
                );
                listaProductos.add(p);
            }

            copiaProductos.addAll(listaProductos);
            lista.setAdapter(new AdaptadorProducto(this, listaProductos));
            registerForContextMenu(lista);

        } catch (Exception e) {
            mostrarMsg("Error al mostrar la lista: " + e.getMessage());
        }
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}