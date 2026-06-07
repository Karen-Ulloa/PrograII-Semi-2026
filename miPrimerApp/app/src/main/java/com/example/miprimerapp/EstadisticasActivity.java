package com.example.miprimerapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;

public class EstadisticasActivity extends Activity {


    private static final String CANAL_ID       = "alerta_emocional";
    private static final int    NOTIF_ID       = 1001;
    private static final int    PERM_REQUEST   = 2001;


    DB          db;
    LinearLayout btnSalir;
    LinearLayout btnEnviarCorreo;
    TextView     txtEstado;
    TextView     txtResumen;
    TextView     txtPorcentaje;
    ProgressBar  progressEmocion;


    int felices       = 0;
    int tranquilos    = 0;
    int tristes       = 0;
    int porcentajeFeliz = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        db = new DB(this);

        txtEstado       = findViewById(R.id.txtEstado);
        txtResumen      = findViewById(R.id.txtResumen);
        txtPorcentaje   = findViewById(R.id.txtPorcentaje);
        progressEmocion = findViewById(R.id.progressEmocion);
        btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        btnSalir        = findViewById(R.id.btnSalir);

        crearCanalNotificacion(); // Crear canal (obligatorio en Android 8+)
        cargarEstadisticas();

        btnEnviarCorreo.setOnClickListener(v -> enviarNotificacionPadre());
        btnSalir.setOnClickListener(v -> finish());
    }


    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    "Alertas emocionales",
                    NotificationManager.IMPORTANCE_HIGH  // aparece como banner emergente
            );
            canal.setDescription("Notificaciones para el padre o tutor");
            canal.enableVibration(true);
            canal.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // visible en pantalla bloqueada

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }
    }

    // ────────────────────────────────────────────────────────────
    //  ENVIAR NOTIFICACIÓN AL PADRE
    // ────────────────────────────────────────────────────────────
    private void enviarNotificacionPadre() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERM_REQUEST);
                return;
            }
        }


        String titulo  = "⚠️ Alerta emocional del niño";
        String resumen = "😄 Felices: "    + felices    +
                "  🙂 Tranquilos: " + tranquilos  +
                "  😢 Tristes: "  + tristes;
        String detalle = "😄 Felices: "     + felices     + "\n" +
                "🙂 Tranquilos: "  + tranquilos  + "\n" +
                "😢 Tristes: "     + tristes     + "\n" +
                "📊 Positividad: " + porcentajeFeliz + "%";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)  // ícono en barra de estado
                .setContentTitle(titulo)
                .setContentText(resumen)
                // Texto expandido al deslizar la notificación hacia abajo
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detalle))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)       // se descarta al tocarla
                .setOngoing(false);        // el niño NO puede descartarla con swipe si pones true

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(NOTIF_ID, builder.build());
            Toast.makeText(this, "✅ Notificación enviada al padre", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enviarNotificacionPadre(); // reintento tras conceder permiso
        } else {
            Toast.makeText(this,
                    "Permiso denegado. No se pudo enviar la notificación.",
                    Toast.LENGTH_LONG).show();
        }
    }


    private void cargarEstadisticas() {
        try {
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM producto", null);

            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null) cursor.close();
                txtEstado.setText("Sin registros");
                txtResumen.setText("No hay datos 😢");
                txtPorcentaje.setText("0%");
                progressEmocion.setProgress(0);
                return;
            }

            felices    = 0;
            tranquilos = 0;
            tristes    = 0;

            while (cursor.moveToNext()) {
                double ganancia = 0;
                try {
                    ganancia = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("ganancia"));
                } catch (Exception e) {
                    ganancia = 0;
                }

                if (ganancia >= 50) {
                    felices++;
                } else if (ganancia >= 20) {
                    tranquilos++;
                } else {
                    tristes++;
                }
            }
            cursor.close();

            int total = felices + tranquilos + tristes;
            porcentajeFeliz = total > 0
                    ? (int) Math.round(((double) felices / total) * 100)
                    : 0;

            progressEmocion.setProgress(porcentajeFeliz);
            txtPorcentaje.setText(porcentajeFeliz + "%");

            if (porcentajeFeliz >= 70) {
                txtEstado.setText("😄 Excelente");
            } else if (porcentajeFeliz >= 40) {
                txtEstado.setText("🙂 Estable");
            } else {
                txtEstado.setText("😢 Bajo");
            }

            txtResumen.setText(
                    "😄 Felices: "     + felices     + "\n" +
                            "🙂 Tranquilos: "  + tranquilos  + "\n" +
                            "😢 Tristes: "     + tristes     + "\n\n" +
                            "📊 Positividad: " + porcentajeFeliz + "%"
            );

            if (porcentajeFeliz < 40) {

                enviarNotificacionPadre();

                Intent intent =
                        new Intent(
                                EstadisticasActivity.this,
                                ConsejosActivity.class
                        );

                intent.putExtra(
                        "porcentaje",
                        porcentajeFeliz
                );

                startActivity(intent);
            }

        } catch (Exception e) {
            txtResumen.setText("ERROR:\n" + e.getMessage());
        }
    }
}