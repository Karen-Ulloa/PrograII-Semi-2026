package com.example.miprimeraapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "producto";
    private static final int DATABASE_VERSION = 2;

    private static final String SQLdb =
            "CREATE TABLE producto (" +
                    "idAmigo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "codigo TEXT, " +
                    "descripcion TEXT, " +
                    "marca TEXT, " +
                    "presentacion TEXT, " +
                    "costo REAL, " +
                    "precio REAL, " +
                    "stock INTEGER, " +
                    "urlFoto TEXT)";

    public DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLdb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS producto");
        onCreate(db);
    }

    public String administrar_amigos(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String mensaje = "ok";
            String sql = "";

            switch (accion){


                case "nuevo":

                    double costo = Double.parseDouble(datos[5]);
                    double precio = costo * 1.40;

                    sql = "INSERT INTO producto (codigo, descripcion, marca, presentacion, costo, precio, stock, urlFoto) VALUES (" +
                            "'" + datos[1] + "'," +
                            "'" + datos[2] + "'," +
                            "'" + datos[3] + "'," +
                            "'" + datos[4] + "'," +
                            costo + "," +
                            precio + "," +
                            datos[6] + "," +
                            "'" + datos[7] + "'" +
                            ")";
                    break;


                case "modificar":

                    double costoM = Double.parseDouble(datos[5]);
                    double precioM = costoM * 1.40;

                    sql = "UPDATE producto SET " +
                            "codigo='" + datos[1] + "', " +
                            "descripcion='" + datos[2] + "', " +
                            "marca='" + datos[3] + "', " +
                            "presentacion='" + datos[4] + "', " +
                            "costo=" + costoM + ", " +
                            "precio=" + precioM + ", " +
                            "stock=" + datos[6] + ", " +
                            "urlFoto='" + datos[7] + "' " +
                            "WHERE idAmigo='" + datos[0] + "'";
                    break;


                case "eliminar":
                    sql = "DELETE FROM producto WHERE idAmigo='" + datos[0] + "'";
                    break;
            }

            db.execSQL(sql);
            db.close();
            return mensaje;

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Cursor lista_amigos(){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM producto", null);
    }
}