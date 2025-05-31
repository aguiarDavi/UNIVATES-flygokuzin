package com.application.flygokuzin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private TextView recordeScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btnIniciar = findViewById(R.id.btnIniciar);
        btnIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
        });

        recordeScore = findViewById(R.id.recordeScore); // apenas vincula aqui
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza recorde sempre que voltar pra tela
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        int recorde = prefs.getInt("recorde", 0);
        if (recordeScore != null) {
            recordeScore.setText("Record Score: " + recorde);
        }
    }
}
