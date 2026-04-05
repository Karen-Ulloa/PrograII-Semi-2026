package com.example.miprimeraapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdaptadorProducto extends BaseAdapter {

    Context context;
    ArrayList<producto> listaProductos;
    LayoutInflater inflater;

    public AdaptadorProducto(Context context, ArrayList<producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaProductos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaProductos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position; // mejor que 0
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fotos, parent, false);
        }

        try {
            producto producto = listaProductos.get(position);

            TextView lblCodigo = convertView.findViewById(R.id.lblcodigoAdaptador);
            TextView lblDescripcion = convertView.findViewById(R.id.lbldescripcionAdaptador);
            TextView lblMarca = convertView.findViewById(R.id.lblmarcaAdaptador);
            ImageView imgFoto = convertView.findViewById(R.id.imgFotoAdaptador);

            lblCodigo.setText(producto.getCodigo());
            lblDescripcion.setText(producto.getDescripcion());
            lblMarca.setText(producto.getMarca());

            Bitmap bitmap = BitmapFactory.decodeFile(producto.getFoto());

            if (bitmap != null) {
                imgFoto.setImageBitmap(bitmap);
            } else {
                imgFoto.setImageResource(android.R.drawable.ic_menu_report_image);
            }

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return convertView;
    }
}