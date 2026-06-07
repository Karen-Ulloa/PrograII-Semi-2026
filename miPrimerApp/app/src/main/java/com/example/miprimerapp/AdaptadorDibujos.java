package com.example.miprimerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.util.ArrayList;

public class AdaptadorDibujos extends BaseAdapter {

    Context context;
    ArrayList<String> dibujos;
    DB db;

    public AdaptadorDibujos(
            Context context,
            ArrayList<String> dibujos
    ) {

        this.context = context;
        this.dibujos = dibujos;
        db = new DB(context);
    }

    @Override
    public int getCount() {
        return dibujos.size();
    }

    @Override
    public Object getItem(int position) {
        return dibujos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        View vista = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_dibujo,
                        parent,
                        false
                );

        ImageView img =
                vista.findViewById(R.id.imgDibujo);

        TextView txtNombre =
                vista.findViewById(R.id.txtNombre);

        Button btnEliminar =
                vista.findViewById(R.id.btnEliminar);

        Button btnEditar =
                vista.findViewById(R.id.btnEditar);

        String ruta =
                dibujos.get(position);

        File archivo =
                new File(ruta);


        if (archivo.exists()) {

            img.setImageDrawable(null);

            BitmapFactory.Options opciones =
                    new BitmapFactory.Options();

            opciones.inMutable = true;

            Bitmap bitmap =
                    BitmapFactory.decodeFile(
                            ruta,
                            opciones
                    );

            img.setImageBitmap(bitmap);

        } else {

            img.setImageResource(
                    android.R.drawable.ic_menu_report_image
            );
        }


        txtNombre.setText(
                archivo.getName()
        );


        btnEliminar.setOnClickListener(v -> {

            new AlertDialog.Builder(context)

                    .setTitle("Eliminar dibujo")

                    .setMessage(
                            "¿Deseas eliminar este dibujo?"
                    )

                    .setPositiveButton(
                            "Sí",
                            (dialog, which) -> {

                                try {

                                    if (archivo.exists()) {
                                        archivo.delete();
                                    }

                                    db.eliminarDibujo(ruta);

                                    dibujos.remove(position);

                                    notifyDataSetChanged();

                                    Toast.makeText(
                                            context,
                                            "Dibujo eliminado 🗑",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } catch (Exception e) {

                                    Toast.makeText(
                                            context,
                                            "Error eliminando",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    )

                    .setNegativeButton(
                            "Cancelar",
                            null
                    )

                    .show();
        });


        btnEditar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            DibujoActivity.class
                    );

            intent.putExtra(
                    "rutaEditar",
                    ruta
            );

            context.startActivity(intent);
        });

        return vista;
    }
}