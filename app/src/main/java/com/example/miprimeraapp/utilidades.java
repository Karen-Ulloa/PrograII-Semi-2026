package com.example.miprimeraapp;

import android.util.Base64;

public class utilidades {

    static String url_consulta = "http://192.168.1.4:5984/karen/_all_docs?include_docs=true";
    static String url_mto = "http://192.168.1.4:5984/karen";

    static String user = "Karen";
    static String passwd = "karen10";

    static String credencialesCodificadas = Base64.encodeToString((user + ":" + passwd).getBytes(), Base64.NO_WRAP);

    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}

