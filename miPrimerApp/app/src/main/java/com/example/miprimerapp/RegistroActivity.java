package com.example.miprimerapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistroActivity extends Activity {

    EditText txtUser, txtPass;
    Button btnRegistrar, btnVolver;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = new DB(this);

        txtUser = findViewById(R.id.txtUserReg);
        txtPass = findViewById(R.id.txtPassReg);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolverLogin);


        btnRegistrar.setOnClickListener(v -> registrar());


        btnVolver.setOnClickListener(v -> finish());
    }

    private void registrar() {

        String user = txtUser.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        if (db.existeUsuario(user)) {
            Toast.makeText(this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
            return;
        }


        db.insertarUsuario(user, pass);

        Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show();
        finish();
    }
}