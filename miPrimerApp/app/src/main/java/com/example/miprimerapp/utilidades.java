package com.example.miprimerapp;

import java.util.Base64;
public class utilidades {
    static String url_consulta = "http://10.70.156.152:5984/michelle/_design/saul/_view/saul";
    static String url_mto = "http://10.70.156.152:5984/michelle"; //CRUD, Insertar, Actualizar, Borrar, y Buscar
    static String user = "michelle";
    static String passwd = "12345";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user +":"+ passwd).getBytes());
    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}
