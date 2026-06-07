package com.example.miprimerapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConsejosActivity extends Activity {

    TextView txtConsejo;

    LinearLayout btnSalir;
    LinearLayout btnEntendido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos);

        txtConsejo = findViewById(R.id.txtConsejo);

        btnSalir = findViewById(R.id.btnSalir);
        btnEntendido = findViewById(R.id.btnEntendido);

        btnSalir.setOnClickListener(v -> finish());

        btnEntendido.setOnClickListener(v -> finish());

        int porcentaje =
                getIntent().getIntExtra(
                        "porcentaje",
                        0
                );

        if (porcentaje < 20) {

            txtConsejo.setText(
                    "😢 Hemos notado que tuviste días difíciles.\n\n" +
                            "🌈 Habla con mamá o papá.\n" +
                            "🎨 Dibuja lo que sientes.\n" +
                            "⚽ Juega algo que te guste.\n" +
                            "😊 Recuerda que no estás solo."
            );

        } else {

            txtConsejo.setText(
                    "🙂 Tu ánimo puede mejorar.\n\n" +
                            "🌳 Sal a caminar.\n" +
                            "🎵 Escucha música divertida.\n" +
                            "👨‍👩‍👧 Comparte tiempo con tu familia.\n" +
                            "⭐ Sigue haciendo cosas que te hacen feliz."
            );
        }
    }
}