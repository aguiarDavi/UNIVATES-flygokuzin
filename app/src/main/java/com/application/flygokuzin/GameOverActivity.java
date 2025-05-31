package com.application.flygokuzin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        int score = getIntent().getIntExtra("score", 0);

        TextView scoreText = findViewById(R.id.textScoreFinal);
        Button restartButton = findViewById(R.id.btnRestart);
        Button btnTelaInicial = findViewById(R.id.btnTelaInicial);

        scoreText.setText("Score Final: " + score);

        // SALVAR RECORD SE NECESSÁRIO
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        int recordeSalvo = prefs.getInt("recorde", 0);
        if (score > recordeSalvo) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("recorde", score);
            editor.apply();
        }

        restartButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnTelaInicial.setOnClickListener(v -> {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        });
    }
}
