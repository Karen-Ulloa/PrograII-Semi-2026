package com.example.miprimeraapp;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnviarDatosServidor extends AsyncTask<String, String, String> {
    Context context;
    HttpURLConnection httpURLConnection;

    public EnviarDatosServidor(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... parametros) {
        String jsonResponse = "";
        String jsonDatos = parametros[0];
        String metodo = parametros[1];
        String _url = parametros[2];

        try {
            URL url = new URL(_url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(metodo);

            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            httpURLConnection.setRequestProperty("Authorization", "Basic " + utilidades.credencialesCodificadas);

            Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(jsonDatos);
            writer.close();

            int statusCode = httpURLConnection.getResponseCode();
            InputStream inputStream;

            if (statusCode >= 200 && statusCode < 300) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            if (inputStream == null) return null;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuffer = new StringBuilder();
            String linea;

            while ((linea = bufferedReader.readLine()) != null) {
                stringBuffer.append(linea);
            }

            jsonResponse = stringBuffer.toString();
            return jsonResponse;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
