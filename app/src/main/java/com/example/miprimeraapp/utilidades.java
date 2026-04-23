package com.example.miprimeraapp;

import java.util.Base64;

public class utilidades {
    static String url_consulta = "http://192.168.56.1:5984/dbamigos/_design/dbamigos/_view/dbamigos";
    static String url_mto = "http://192.168.56.1:5984/dbamigos"; //CRUD, Insertar, Actualizar, Borrar, y Buscar
    static String user = "Karen";
    static String passwd = "karen10";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}

