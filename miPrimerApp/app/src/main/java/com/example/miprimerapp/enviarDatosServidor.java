package com.example.miprimerapp;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class enviarDatosServidor extends AsyncTask<String, String, String> {

    Context context;
    HttpURLConnection httpURLConnection;

    public enviarDatosServidor(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... parametros) {

        String jsonResponse = "";

        String jsonDatos = parametros[0];
        String metodo = parametros[1];
        String _url = parametros[2];

        BufferedReader bufferedReader = null;

        try {

            URL url = new URL(_url);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setRequestMethod(metodo);

            httpURLConnection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            httpURLConnection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            httpURLConnection.setRequestProperty(
                    "Authorization",
                    "Basic " + utilidades.credencialesCodificadas
            );

            if (!metodo.equals("DELETE")) {

                Writer writer = new BufferedWriter(
                        new OutputStreamWriter(
                                httpURLConnection.getOutputStream(),
                                "UTF-8"
                        )
                );

                writer.write(jsonDatos);
                writer.flush();
                writer.close();
            }

            int codigo = httpURLConnection.getResponseCode();

            InputStream inputStream;

            if (codigo >= 200 && codigo < 300) {
                inputStream = new BufferedInputStream(
                        httpURLConnection.getInputStream()
                );
            } else {
                inputStream = new BufferedInputStream(
                        httpURLConnection.getErrorStream()
                );
            }

            bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            String linea;
            StringBuilder stringBuilder = new StringBuilder();

            while ((linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea);
            }

            jsonResponse = stringBuilder.toString();

        } catch (Exception e) {

            return "{\"error\":\"" + e.getMessage() + "\"}";

        } finally {

            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return jsonResponse;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}