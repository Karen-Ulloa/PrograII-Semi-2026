package com.example.miprimeraapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView txtCantidad, lblRespuesta;
    Spinner spnDe, spnA;
    Button btnConvertir;


    Double valores[][] = {

            {1.0, 10.7639, 1.4311, 1.19599, 0.00159033, 0.0001431, 1e-4}
    };

    String[][] etiquetas = {
            {"Metro Cuadrado", "Pie Cuadrado", "Vara Cuadrada", "Yarda Cuadrada", "Tarea", "Manzana", "Hectárea"}
    };


    EditText etMetros;
    Button btnCalcular;
    TextView tvResultadoAgua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarTabs();
        configurarAgua();
        configurarConversor();
    }


    private void configurarTabs() {

        TabHost tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec;

        spec = tabHost.newTabSpec("Tab1");
        spec.setContent(R.id.Calculo_de_agua);
        spec.setIndicator("AGUA");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tab2");
        spec.setContent(R.id.Area);
        spec.setIndicator("AREA");
        tabHost.addTab(spec);
    }

    private void configurarAgua() {

        etMetros = findViewById(R.id.etMetros);
        btnCalcular = findViewById(R.id.btnCalcular);
        tvResultadoAgua = findViewById(R.id.tvResultado);

        btnCalcular.setOnClickListener(v -> {

            String texto = etMetros.getText().toString();

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingresa los metros consumidos", Toast.LENGTH_SHORT).show();
                return;
            }

            double metros;

            try {
                metros = Double.parseDouble(texto);
            } catch (Exception e) {
                Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            double total;

            if (metros <= 18) {
                total = 6.0;
            } else if (metros <= 28) {
                total = 6 + ((metros - 18) * 0.45);
            } else {
                double tramo1 = 10 * 0.45;
                double tramo2 = (metros - 28) * 0.65;
                total = 6 + tramo1 + tramo2;
            }

            tvResultadoAgua.setText("RESULTADO: $" + String.format("%.2f", total));
            Toast.makeText(this, "Cálculo realizado", Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarConversor() {

        btnConvertir = findViewById(R.id.btnConvertir);
        spnDe = findViewById(R.id.spnDe);
        spnA = findViewById(R.id.spnA);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                etiquetas[0]
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnDe.setAdapter(adapter);
        spnA.setAdapter(adapter);

        btnConvertir.setOnClickListener(v -> convertir());
    }

    private void convertir() {

        txtCantidad = findViewById(R.id.etArea);
        String texto = txtCantidad.getText().toString();

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

        int de = spnDe.getSelectedItemPosition();
        int a = spnA.getSelectedItemPosition();

        double respuesta = conversor(de, a, cantidad);

        lblRespuesta = findViewById(R.id.tvResultadoArea);
        lblRespuesta.setText("RESULTADO: " + respuesta);

        Toast.makeText(this, "Conversión realizada", Toast.LENGTH_SHORT).show();
    }

    private double conversor(int de, int a, double cantidad) {

        return valores[0][a] / valores[0][de] * cantidad;
    }
}