package com.example.miprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "producto";
    private static final int DATABASE_VERSION = 5;

    private static final String SQLdb =
            "CREATE TABLE producto (" +
                    "idProducto TEXT PRIMARY KEY, " + // ← TEXT, no AUTOINCREMENT
                    "codigo TEXT, " +
                    "descripcion TEXT, " +
                    "marca TEXT, " +
                    "presentacion TEXT, " +
                    "costo REAL, " +
                    "precio REAL, " +
                    "ganancia REAL, " +
                    "stock INTEGER, " +
                    "foto TEXT, " +
                    "pendiente_sync INTEGER DEFAULT 0)";

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
    public String administrar_productos(String accion, String[] datos) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "";

            switch (accion) {

                case "nuevo":
                    double costo    = Double.parseDouble(datos[5]);
                    double precio   = Double.parseDouble(datos[6]);
                    int    stock    = Integer.parseInt(datos[7]);
                    double ganancia = (costo > 0) ? ((precio - costo) / costo) * 100 : 0;

                    sql = "INSERT INTO producto (idProducto, codigo, descripcion, marca, presentacion, costo, precio, ganancia, stock, foto, pendiente_sync) VALUES (" +
                            "'" + datos[0] + "'," +
                            "'" + datos[1] + "'," +
                            "'" + datos[2] + "'," +
                            "'" + datos[3] + "'," +
                            "'" + datos[4] + "'," +
                            costo + "," +
                            precio + "," +
                            ganancia + "," +
                            stock + "," +
                            "'" + datos[8] + "'," +
                            "0)";
                    break;

                case "modificar":
                    double Costo    = Double.parseDouble(datos[5]);
                    double Precio   = Double.parseDouble(datos[6]);
                    int    Stock    = Integer.parseInt(datos[7]);
                    double Ganancia = (Costo > 0) ? ((Precio - Costo) / Costo) * 100 : 0;

                    sql = "UPDATE producto SET " +
                            "codigo='"       + datos[1] + "'," +
                            "descripcion='"  + datos[2] + "'," +
                            "marca='"        + datos[3] + "'," +
                            "presentacion='" + datos[4] + "'," +
                            "costo="         + Costo    + "," +
                            "precio="        + Precio   + "," +
                            "ganancia="      + Ganancia + "," +
                            "stock="         + Stock    + "," +
                            "foto='"         + datos[8] + "'" +
                            " WHERE idProducto='" + datos[0] + "'";
                    break;

                case "eliminar":
                    sql = "DELETE FROM producto WHERE idProducto='" + datos[0] + "'";
                    break;
            }

            db.execSQL(sql);
            db.close();
            return "ok";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public Cursor lista_productos() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM producto", null);
    }

    public void marcarPendienteSyncPorIdProducto(String idProducto) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("pendiente_sync", 1);
            db.update("producto", cv, "idProducto=?", new String[]{idProducto});
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> obtenerPendientesSync() {
        List<String[]> lista = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery(
                    "SELECT idProducto, codigo, descripcion, marca, presentacion, " +
                            "costo, precio, stock, foto FROM producto WHERE pendiente_sync = 1", null);

            while (c.moveToNext()) {
                lista.add(new String[]{
                        c.getString(0), // idProducto
                        c.getString(1), // codigo
                        c.getString(2), // descripcion
                        c.getString(3), // marca
                        c.getString(4), // presentacion
                        c.getString(5), // costo
                        c.getString(6), // precio
                        c.getString(7), // stock
                        c.getString(8)  // foto
                });
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void eliminarPendienteSync(String idProducto) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("pendiente_sync", 0);
            db.update("producto", cv, "idProducto=?", new String[]{idProducto});
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}