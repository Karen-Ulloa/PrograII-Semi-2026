package com.example.miprimeraapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    RadioButton opt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(v->calcular());
    }
    private void calcular() {
        tempVal = findViewById(R.id.txtNum1);
        Double num1 = Double.parseDouble(tempVal.getText().toString());

        tempVal = findViewById(R.id.txtNum2);
        Double num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = 0;

        opt = findViewById(R.id.optSuma);
        if (opt.isChecked()) {
            respuesta = num1 + num2;
        }

        opt = findViewById(R.id.optResta);
            if (opt.isChecked()) {
                respuesta = num1 - num2;
            }

        opt = findViewById(R.id.optMultiplicar);
                if (opt.isChecked()) {
                    respuesta = num1 * num2;
                }
        opt = findViewById(R.id.optDividir);
                    if (opt.isChecked()) {
                        respuesta = num1 / num2;
                    }
        opt = findViewById(R.id.optFactorial);
        if (opt.isChecked()) {
            double fact = 1;
            for(int i = 1; i <= num1; i++) {
                fact = fact * i;
            }
            respuesta = fact;
        }
        opt = findViewById(R.id.optPotencia);
         if(opt.isChecked()) {
             double base = num1;
             double exponente = num2;
             respuesta = Math.pow(base, exponente);
         }
        opt = findViewById(R.id.optPorcentaje);

        if(opt.isChecked()) {

            double total = num1;      // número base
            double por = num2;        // porcentaje

            respuesta = (total * por) / 100;
        }
        opt = findViewById(R.id.optRaiz);

        if(opt.isChecked()) {

            double numero = num1;   // número
            double indice = num2;   // tipo de raíz (2,3,4...)

            if(indice == 0){
                tempVal.setText("Error");
                return;
            }
            respuesta = Math.pow(numero, 1.0 / indice);

        }
                        tempVal = findViewById(R.id.lblRespuesta);
                        tempVal.setText("Respuesta: " + respuesta);


                    }
                }