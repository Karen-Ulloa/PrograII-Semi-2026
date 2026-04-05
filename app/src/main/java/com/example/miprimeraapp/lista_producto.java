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
    ListView ltsAmigos;
    Cursor cAmigos;

    final ArrayList<producto> alAmigos = new ArrayList<>();
    final ArrayList<producto> alProductoCopia = new ArrayList<>();
    JSONArray jsonArray;
    JSONObject jsonObject;

    AdaptadorProducto adaptador;
    int posicion = 0;
    producto misProducto;

    Bundle parametros = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_producto);

        db = new DB(this);
        parametros.putString("accion", "nuevo");

        fab = findViewById(R.id.fabAgregarAmigos);
        fab.setOnClickListener(v -> abrirActivity());

        ltsAmigos = findViewById(R.id.ltsAmigos);

        // Inicializar adaptador
        adaptador = new AdaptadorProducto(this, alAmigos);
        ltsAmigos.setAdapter(adaptador);
        registerForContextMenu(ltsAmigos);

        obtenerAmigos();
        buscarAmigos();
    }

    // 🔹 Context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(alAmigos.get(posicion).getCodigo());
        } catch (Exception e) {
            mostrarMsg("Error al desplegar menú: " + e.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.mnxAgregar) {
                abrirActivity();
            } else if (item.getItemId() == R.id.mnxModificar) {
                parametros.putString("accion", "modificar");
                parametros.putString("amigos", crearJSONObject(alAmigos.get(posicion)).toString());
                abrirActivity();
            } else if (item.getItemId() == R.id.mnxEliminar) {
                borrarAmigo();
            }
            return true;
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar un item del menú: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }


    private void borrarAmigo() {
        try {
            String codigo = alAmigos.get(posicion).getCodigo();
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("¿Está seguro de borrar?");
            confirmacion.setMessage(codigo);

            confirmacion.setPositiveButton("SI", (dialog, which) -> {
                try {
                    String respuesta = db.administrar_amigos("eliminar",
                            new String[]{alAmigos.get(posicion).getIdProducto()});

                    if (respuesta.equals("ok")) {
                        // Eliminar del ArrayList
                        alAmigos.remove(posicion);
                        alProductoCopia.remove(posicion);

                        // Notificar al adaptador
                        adaptador.notifyDataSetChanged();

                        mostrarMsg("Producto borrado con éxito.");
                    }
                } catch (Exception e) {
                    mostrarMsg(e.getMessage());
                }
            });

            confirmacion.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            confirmacion.create().show();

        } catch (Exception e) {
            mostrarMsg("Error al borrar el producto: " + e.getMessage());
        }
    }


    private void buscarAmigos() {
        TextView txtBuscar = findViewById(R.id.txtBuscarAmigos);
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alAmigos.clear();
                String buscar = txtBuscar.getText().toString().trim().toLowerCase();
                if (buscar.isEmpty()) {
                    alAmigos.addAll(alProductoCopia);
                } else {
                    for (producto item : alProductoCopia) {
                        if (item.getCodigo().toLowerCase().contains(buscar)
                                || item.getDescripcion().toLowerCase().contains(buscar)
                                || item.getMarca().toLowerCase().contains(buscar)) {
                            alAmigos.add(item);
                        }
                    }
                }
                adaptador.notifyDataSetChanged();
            }
        });
    }


    private void abrirActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(parametros);
        startActivity(intent);
    }


    private void obtenerAmigos() {
        try {
            cAmigos = db.lista_amigos();
            if (cAmigos.moveToFirst()) {
                alAmigos.clear();
                alProductoCopia.clear();

                do {
                    misProducto = new producto(
                            cAmigos.getString(0),
                            cAmigos.getString(1),
                            cAmigos.getString(2),
                            cAmigos.getString(3),
                            cAmigos.getString(4),
                            cAmigos.getString(5),
                            cAmigos.getString(6)
                    );
                    alAmigos.add(misProducto);
                } while (cAmigos.moveToNext());

                alProductoCopia.addAll(alAmigos);
                adaptador.notifyDataSetChanged();
            } else {
                mostrarMsg("No hay productos que mostrar.");
            }
        } catch (Exception e) {
            mostrarMsg(e.getMessage());
        }
    }


    private JSONObject crearJSONObject(producto p) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("idAmigo", p.getIdProducto());
            obj.put("codigo", p.getCodigo());
            obj.put("descripcion", p.getDescripcion());
            obj.put("marca", p.getMarca());
            obj.put("presentacion", p.getPresentacion());
            obj.put("precio", p.getPrecio());
            obj.put("foto", p.getFoto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
