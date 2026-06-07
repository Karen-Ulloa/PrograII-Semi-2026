package com.example.miprimerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "producto.db";
    private static final int DATABASE_VERSION = 10;

    public DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLA PRODUCTOS
        db.execSQL(
                "CREATE TABLE producto (" +
                        "idAmigo INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "idProducto TEXT," +
                        "codigo TEXT," +
                        "descripcion TEXT," +
                        "marca TEXT," +
                        "presentacion TEXT," +
                        "precio TEXT," +
                        "urlFoto TEXT," +
                        "costo TEXT," +
                        "stock TEXT," +
                        "ganancia TEXT," +
                        "emocion TEXT)"
        );

        // TABLA USUARIOS
        db.execSQL(
                "CREATE TABLE usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "usuario TEXT," +
                        "password TEXT)"
        );

        // TABLA DIBUJOS
        db.execSQL(
                "CREATE TABLE dibujos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "rutaDibujo TEXT)"
        );

        // TABLA ESTADISTICAS
        db.execSQL(
                "CREATE TABLE estadisticas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "emocion TEXT," +
                        "fecha TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS producto");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS dibujos");
        db.execSQL("DROP TABLE IF EXISTS estadisticas");

        onCreate(db);
    }

    // LOGIN
    public boolean login(String usuario, String password) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE usuario=? AND password=?",
                new String[]{usuario, password}
        );

        boolean existe = cursor.moveToFirst();

        cursor.close();
        db.close();

        return existe;
    }

    // INSERTAR USUARIO
    public void insertarUsuario(String usuario, String password) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("usuario", usuario);
        valores.put("password", password);

        db.insert("usuarios", null, valores);

        db.close();
    }

    // EXISTE USUARIO
    public boolean existeUsuario(String user) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE usuario=?",
                new String[]{user}
        );

        boolean existe = cursor.moveToFirst();

        cursor.close();
        db.close();

        return existe;
    }

    // ADMINISTRAR PRODUCTOS
    public String administrar_amigos(String accion, String[] datos) {

        try {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues valores = new ContentValues();

            switch (accion) {

                case "nuevo":

                    valores.put("idProducto", datos[0]);
                    valores.put("codigo", datos[1]);
                    valores.put("descripcion", datos[2]);
                    valores.put("marca", datos[3]);
                    valores.put("presentacion", datos[4]);
                    valores.put("precio", datos[5]);
                    valores.put("urlFoto", datos[6]);
                    valores.put("costo", datos[7]);
                    valores.put("stock", datos[8]);
                    valores.put("ganancia", datos[9]);
                    valores.put("emocion", datos[10]);

                    db.insert("producto", null, valores);

                    // GUARDAR ESTADISTICA
                    ContentValues estadistica =
                            new ContentValues();

                    estadistica.put(
                            "emocion",
                            datos[10]
                    );

                    estadistica.put(
                            "fecha",
                            String.valueOf(
                                    System.currentTimeMillis()
                            )
                    );

                    db.insert(
                            "estadisticas",
                            null,
                            estadistica
                    );

                    break;

                case "modificar":

                    valores.put("codigo", datos[1]);
                    valores.put("descripcion", datos[2]);
                    valores.put("marca", datos[3]);
                    valores.put("presentacion", datos[4]);
                    valores.put("precio", datos[5]);
                    valores.put("urlFoto", datos[6]);
                    valores.put("costo", datos[7]);
                    valores.put("stock", datos[8]);
                    valores.put("ganancia", datos[9]);
                    valores.put("emocion", datos[10]);

                    db.update(
                            "producto",
                            valores,
                            "idProducto=?",
                            new String[]{datos[0]}
                    );

                    break;

                case "eliminar":

                    db.delete(
                            "producto",
                            "idProducto=?",
                            new String[]{datos[0]}
                    );

                    break;
            }

            db.close();

            return "ok";

        } catch (Exception e) {

            return e.getMessage();
        }
    }

    // LISTA PRODUCTOS
    public Cursor lista_amigos() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM producto",
                null
        );
    }

    // GUARDAR DIBUJO
    public void guardarDibujo(String rutaDibujo) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores =
                new ContentValues();

        valores.put(
                "rutaDibujo",
                rutaDibujo
        );

        db.insert(
                "dibujos",
                null,
                valores
        );

        db.close();
    }

    // OBTENER DIBUJOS
    public Cursor obtenerDibujos() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM dibujos ORDER BY id DESC",
                null
        );
    }

    // CONTAR DIBUJOS
    public int contarDibujos() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM dibujos",
                null
        );

        int total = 0;

        if (cursor.moveToFirst()) {

            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    // ESTADISTICAS
    public Cursor obtenerEstadisticas() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT emocion, COUNT(*) total " +
                        "FROM producto " +
                        "GROUP BY emocion",
                null
        );
    }


    public void eliminarDibujo(String ruta) {

        SQLiteDatabase db =
                getWritableDatabase();

        db.delete(
                "dibujos",
                "rutaDibujo=?",
                new String[]{ruta}
        );

        db.close();
    }







    public Cursor obtenerAmigos() {

        SQLiteDatabase database =
                this.getReadableDatabase();

        return database.rawQuery(
                "SELECT * FROM producto",
                null
        );
    }





    public Cursor obtenerDatos() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM producto",
                null
        );
    }
}