package com.example.miprimeraapp;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class obtenerDatosServidor extends AsyncTask<String, String, String> {
    HttpURLConnection httpURLConnection;

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder respuesta = new StringBuilder();
        try {

            URL url = new URL(utilidades.url_consulta);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);
            httpURLConnection.setRequestProperty("Accept", "application/json");

            int status = httpURLConnection.getResponseCode();
            InputStream inputStream;

            if (status >= 200 && status < 300) {
                inputStream = httpURLConnection.getInputStream();
            } else {

                inputStream = httpURLConnection.getErrorStream();
            }

            if (inputStream == null) return "Error: No hay flujo de entrada";

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                respuesta.append(linea);
            }

            return respuesta.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}