package com.example.miprimeraapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tempVal;
    Spinner spn;
    Button btn;

    Double valores[][] = {

            // MONEDAS (base dólar)
            {1.0, 0.85, 7.67, 26.42, 36.80, 495.77},

            // LONGITUD (base metro)
            {1.0, 0.001, 100.0, 39.3701, 3.28084, 1.1963, 1.09361},

            // VOLUMEN (base litro)
            {1.0, 1000.0, 0.264172, 0.0353147, 0.001},

            // MASA (base kilogramo)
            {1.0, 1000.0, 2.20462, 35.274, 0.00100000108},

            // ALMACENAMIENTO (base byte)
            {1.0, 0.001, 1e-6, 1e-9, 1e-12},

            // TIEMPO (base MINUTO)
            {60.0, 1.0, 0.01666668, 0.000694445, 9.9206428571e-5, 1.9026e-6},

            // TRANSFERENCIA DE DATOS (base bps)
            {1.0, 0.001, 1e-6, 1e-9}
    };

    String[][] etiquetas = {

            // MONEDAS
            {"Dolar", "Euro", "Quetzal", "Lempira", "Cordoba", "Colon CR"},

            // LONGITUD
            {"Metro", "Kilometro", "Centimetro", "Pulgada", "Pie", "Vara", "Yarda"},

            // VOLUMEN
            {"Litro", "Mililitro", "Galon", "Pie cubico", "Metro cubico"},

            // MASA
            {"Kilogramo", "Gramo", "Libra", "Onza", "Tonelada"},

            // ALMACENAMIENTO
            {"Byte", "Kilobyte", "Megabyte", "Gigabyte", "Terabyte"},

            // TIEMPO
            {"Segundo", "Minuto", "Hora", "Dia", "Semana", "Año"},

            // TRANSFERENCIA DE DATOS
            {"Bits por segundo", "Kilobits por segundo", "Megabits por segundo", "Gigabits por segundo"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnConvertir);
        btn.setOnClickListener(v -> convertir());

        cambiarEtiqueta(0);

        spn = findViewById(R.id.spnTipo);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cambiarEtiqueta(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void cambiarEtiqueta(int posicion) {
        ArrayAdapter<String> aaEtiquetas = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                etiquetas[posicion]
        );
        aaEtiquetas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spn = findViewById(R.id.spnDe);
        spn.setAdapter(aaEtiquetas);

        spn = findViewById(R.id.spnA);
        spn.setAdapter(aaEtiquetas);
    }

    private void convertir() {

        tempVal = findViewById(R.id.txtCantidad);
        String texto = tempVal.getText().toString();

        // VALIDACIÓN
        if (texto.isEmpty()) {
            Toast.makeText(this, "Ingresa una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(texto);
        } catch (Exception e) {
            Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        spn = findViewById(R.id.spnTipo);
        int tipo = spn.getSelectedItemPosition();

        spn = findViewById(R.id.spnDe);
        int de = spn.getSelectedItemPosition();

        spn = findViewById(R.id.spnA);
        int a = spn.getSelectedItemPosition();

        if (valores[tipo][de] == 0) {
            Toast.makeText(this, "Error en unidades", Toast.LENGTH_SHORT).show();
            return;
        }

        double respuesta = conversor(tipo, de, a, cantidad);

        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("Respuesta: " + respuesta);

        Toast.makeText(this, "Conversión realizada", Toast.LENGTH_SHORT).show();
    }

    private double conversor(int tipo, int de, int a, double cantidad) {
        return valores[tipo][a] / valores[tipo][de] * cantidad;
    }
}