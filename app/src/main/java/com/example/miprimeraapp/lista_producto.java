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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_producto extends Activity {

    DB db;
    FloatingActionButton fab;
    ListView lista;
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

        db = new DB(this);

        parametros.putString("accion", "nuevo");

        fab = findViewById(R.id.fabAgregarAmigos);
        fab.setOnClickListener(v -> abrirFormulario());

        di = new detectarInternet(this);

        obtenerProductos();
        buscarProductos();
    }


    @Override
    public void onCreateContextMenu(
            ContextMenu menu,
            View v,
            ContextMenu.ContextMenuInfo menuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        try {

            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;

            posicion = info.position;

            JSONObject fila =
                    jsonArray.getJSONObject(posicion);

            JSONObject value =
                    fila.optJSONObject("value");

            if (value == null) value = fila;

            menu.setHeaderTitle(
                    value.optString(
                            "descripcion",
                            "Producto"
                    )
            );

        } catch (Exception e) {
            mostrarMsg("Error menú");
        }
    }

    @Override
    public boolean onContextItemSelected(
            @NonNull MenuItem item
    ) {
        try {

            JSONObject fila =
                    jsonArray.getJSONObject(posicion);

            JSONObject value =
                    fila.optJSONObject("value");

            if (value == null) value = fila;

            if (item.getItemId() == R.id.mnxAgregar) {

                parametros.putString(
                        "accion",
                        "nuevo"
                );

                abrirFormulario();

            } else if (item.getItemId()
                    == R.id.mnxModificar) {

                parametros.putString(
                        "accion",
                        "modificar"
                );

                parametros.putString(
                        "producto",
                        value.toString()
                );

                abrirFormulario();

            } else if (item.getItemId()
                    == R.id.mnxEliminar) {

                eliminarProducto(value);
            }

            return true;

        } catch (Exception e) {
            mostrarMsg("Error menú");
            return false;
        }
    }


    private void eliminarProducto(
            JSONObject value
    ) {

        try {

            AlertDialog.Builder dialog =
                    new AlertDialog.Builder(this);

            dialog.setTitle(
                    "Eliminar producto"
            );

            dialog.setMessage(
                    value.optString(
                            "descripcion"
                    )
            );

            dialog.setPositiveButton(
                    "SI",
                    (d, w) -> {

                        try {

                            String idProducto =
                                    value.optString(
                                            "idProducto"
                                    );

                            db.administrar_amigos(
                                    "eliminar",
                                    new String[]{
                                            idProducto
                                    }
                            );

                            // INTERNET
                            if (di.hayConexionInternet()) {

                                String _id =
                                        value.optString(
                                                "_id"
                                        );

                                String _rev =
                                        value.optString(
                                                "_rev"
                                        );

                                if (!_id.isEmpty()
                                        && !_rev.isEmpty()) {

                                    String url =
                                            utilidades.url_mto +
                                                    "/" +
                                                    _id +
                                                    "?rev=" +
                                                    _rev;

                                    EnviarDatosServidor enviar =
                                            new EnviarDatosServidor(
                                                    this
                                            );

                                    enviar.execute(
                                            "{}",
                                            "DELETE",
                                            url
                                    ).get();
                                }
                            }

                            mostrarMsg(
                                    "Producto eliminado"
                            );

                            obtenerProductos();

                        } catch (Exception e) {
                            mostrarMsg(
                                    "Error eliminar"
                            );
                        }
                    });

            dialog.setNegativeButton(
                    "NO",
                    (d, w) -> d.dismiss()
            );

            dialog.show();

        } catch (Exception e) {
            mostrarMsg("Error");
        }
    }


    private void buscarProductos() {

        TextView buscar =
                findViewById(
                        R.id.txtBuscarAmigos
                );

        buscar.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int a,
                            int b,
                            int c
                    ) {
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int a,
                            int b,
                            int c
                    ) {

                        listaProductos.clear();

                        String txt =
                                buscar.getText()
                                        .toString()
                                        .toLowerCase()
                                        .trim();

                        if (txt.isEmpty()) {

                            listaProductos.addAll(
                                    copiaProductos
                            );

                        } else {

                            for (producto p :
                                    copiaProductos) {

                                if (p.getDescripcion()
                                        .toLowerCase()
                                        .contains(txt)

                                        ||

                                        p.getCodigo()
                                                .toLowerCase()
                                                .contains(txt)

                                        ||

                                        p.getMarca()
                                                .toLowerCase()
                                                .contains(txt)) {

                                    listaProductos.add(
                                            p
                                    );
                                }
                            }
                        }

                        lista.setAdapter(
                                new AdaptadorProducto(
                                        getApplicationContext(),
                                        listaProductos
                                )
                        );
                    }
                });
    }


    private void abrirFormulario() {

        Intent i =
                new Intent(
                        this,
                        MainActivity.class
                );

        i.putExtras(parametros);

        startActivity(i);
    }


    private void obtenerProductos() {

        try {

            jsonArray = new JSONArray();

            // INTERNET
            if (di.hayConexionInternet()) {

                datosServidor =
                        new obtenerDatosServidor();

                String resp =
                        datosServidor.execute()
                                .get();

                jsonObject =
                        new JSONObject(resp);

                jsonArray =
                        jsonObject.getJSONArray(
                                "rows"
                        );

            } else {

                cursor =
                        db.lista_amigos();

                if (cursor.moveToFirst()) {

                    do {

                        JSONObject fila =
                                new JSONObject();

                        JSONObject value =
                                new JSONObject();

                        value.put(
                                "idProducto",
                                cursor.getString(0)
                        );

                        value.put(
                                "codigo",
                                cursor.getString(1)
                        );

                        value.put(
                                "descripcion",
                                cursor.getString(2)
                        );

                        value.put(
                                "marca",
                                cursor.getString(3)
                        );

                        value.put(
                                "presentacion",
                                cursor.getString(4)
                        );

                        value.put(
                                "precio",
                                cursor.getString(5)
                        );

                        value.put(
                                "foto",
                                cursor.getString(6)
                        );

                        fila.put(
                                "value",
                                value
                        );

                        jsonArray.put(fila);

                    } while (
                            cursor.moveToNext()
                    );
                }
            }

            mostrarProductos();

        } catch (Exception e) {
            mostrarMsg(
                    "Error cargar datos"
            );
        }
    }


    private void mostrarProductos() {

        try {

            lista =
                    findViewById(
                            R.id.ltsAmigos
                    );

            listaProductos.clear();
            copiaProductos.clear();

            for (int i = 0;
                 i < jsonArray.length();
                 i++) {

                JSONObject fila =
                        jsonArray.getJSONObject(i);

                JSONObject value =
                        fila.optJSONObject(
                                "value"
                        );

                if (value == null)
                    value = fila;

                producto p =
                        new producto(
                                value.optString(
                                        "idProducto"
                                ),
                                value.optString(
                                        "codigo"
                                ),
                                value.optString(
                                        "descripcion"
                                ),
                                value.optString(
                                        "marca"
                                ),
                                value.optString(
                                        "presentacion"
                                ),
                                value.optString(
                                        "precio"
                                ),
                                value.optString(
                                        "foto"
                                )
                        );

                listaProductos.add(p);
            }

            copiaProductos.addAll(
                    listaProductos
            );

            lista.setAdapter(
                    new AdaptadorProducto(
                            this,
                            listaProductos
                    )
            );

            registerForContextMenu(lista);

        } catch (Exception e) {
            mostrarMsg(
                    "Error mostrar lista"
            );
        }
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