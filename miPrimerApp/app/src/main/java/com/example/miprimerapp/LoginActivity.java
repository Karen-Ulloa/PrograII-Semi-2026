package com.example.miprimerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    EditText txtUsuario, txtPassword;
    Button btnLogin, btnIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro); // 👈 aquí

        btnLogin.setOnClickListener(v -> validarLogin());


        btnIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });
    }

    private void validarLogin() {
        String user = txtUsuario.getText().toString().trim();
        String pass = txtPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        DB db = new DB(this);

        if (db.login(user, pass)) {
            startActivity(new Intent(this, lista_producto.class));
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}