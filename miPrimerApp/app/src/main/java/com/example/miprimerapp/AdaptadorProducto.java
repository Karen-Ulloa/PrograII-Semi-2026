package com.example.miprimerapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorProducto extends BaseAdapter {

    private final Context context;
    private final ArrayList<producto> listaProductos;
    private final LayoutInflater inflater;

    public AdaptadorProducto(Context context, ArrayList<producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    @Override
    public producto getItem(int position) {
        return listaProductos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView lblCodigo;
        TextView lblDescripcion;
        TextView lblMarca;
        TextView txtGanancia;
        ImageView imgFoto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fotos, parent, false);
            holder = new ViewHolder();
            holder.lblCodigo     = convertView.findViewById(R.id.lblcodigoAdaptador);
            holder.lblDescripcion = convertView.findViewById(R.id.lbldescripcionAdaptador);
            holder.lblMarca      = convertView.findViewById(R.id.lblmarcaAdaptador);
            holder.txtGanancia   = convertView.findViewById(R.id.txtGanancia);
            holder.imgFoto       = convertView.findViewById(R.id.imgFotoAdaptador);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        producto p = listaProductos.get(position);

        holder.lblCodigo.setText(p.getCodigo());
        holder.lblDescripcion.setText(p.getDescripcion());
        holder.lblMarca.setText(p.getMarca());

        cargarFoto(holder.imgFoto, p.getFoto());
        mostrarGanancia(holder.txtGanancia, p.getGanancia());

        return convertView;
    }

    private void cargarFoto(ImageView imageView, String rutaFoto) {
        if (rutaFoto == null || rutaFoto.trim().isEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(rutaFoto.trim());
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    private void mostrarGanancia(TextView textView, String gananciaStr) {

        double ganancia = 0.0;

        if (gananciaStr != null && !gananciaStr.trim().isEmpty()) {
            try {
                ganancia = Double.parseDouble(gananciaStr.trim());
            } catch (NumberFormatException e) {
                ganancia = 0.0;
            }
        }

        String emocion;
        String estrellas;

        if (ganancia >= 50) {
            emocion = "😄 Muy feliz";
            estrellas = "⭐⭐⭐";
        } else if (ganancia >= 20) {
            emocion = "🙂 Tranquilo";
            estrellas = "⭐⭐";
        } else {
            emocion = "😢 Triste";
            estrellas = "⭐";
        }

        textView.setText(emocion + " " + estrellas);
    }
}