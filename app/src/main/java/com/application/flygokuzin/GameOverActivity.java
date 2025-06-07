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

        // Recupera o score final passado pela tela anterior (MainActivity)
        int score = getIntent().getIntExtra("score", 0);

        // Referências aos elementos da interface
        TextView scoreText = findViewById(R.id.textScoreFinal);
        Button restartButton = findViewById(R.id.btnRestart);
        Button btnTelaInicial = findViewById(R.id.btnTelaInicial);

        // Exibe o score final na tela de Game Over
        scoreText.setText("Score Final: " + score);

        // SALVAR RECORD SE NECESSÁRIO
        // Verifica o recorde salvo nas preferências. Se o novo score for maior, atualiza o recorde.
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        int recordeSalvo = prefs.getInt("recorde", 0);
        if (score > recordeSalvo) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("recorde", score);
            editor.apply();
        }

        // Quando o jogador clica em "REINICIAR", reinicia o jogo voltando para a MainActivity
        restartButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Encerra a tela atual
        });

        // Quando o jogador clica em "TELA INICIAL", volta para a StartActivity (tela de início)
        btnTelaInicial.setOnClickListener(v -> {
            startActivity(new Intent(this, StartActivity.class));
            finish(); // Encerra a tela atual
        });
    }
}
