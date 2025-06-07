package com.application.flygokuzin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private TextView recordeScore;           // Texto que mostra o recorde do jogador
    private MediaPlayer startMusic;          // Música da tela inicial
    private ImageButton btnSom;              // Botão para ligar/desligar o som
    private boolean somLigado = true;        // Estado atual do som (ligado ou desligado)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start); // Define o layout da tela inicial

        Button btnIniciar = findViewById(R.id.btnIniciar);
        // Ao clicar no botão iniciar, para a música e vai para a tela principal (jogo)
        btnIniciar.setOnClickListener(v -> {
            if (startMusic != null) {
                startMusic.stop();           // Para a música
                startMusic.release();        // Libera os recursos
                startMusic = null;
            }
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);          // Abre a tela principal do jogo
        });

        recordeScore = findViewById(R.id.recordeScore); // Apenas vincula o TextView
        btnSom = findViewById(R.id.btnSom);             // Botão de som

        // Alterna o som ligado/desligado ao clicar no botão
        btnSom.setOnClickListener(v -> {
            if (somLigado) {
                if (startMusic != null && startMusic.isPlaying()) {
                    startMusic.pause();                  // Pausa a música
                }
                btnSom.setImageResource(R.drawable.btn_som_off); // Muda ícone
            } else {
                if (startMusic != null) {
                    startMusic.start();                 // Retoma a música
                }
                btnSom.setImageResource(R.drawable.btn_som_on);
            }
            somLigado = !somLigado;  // Inverte o estado do som
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Quando a tela volta a aparecer, atualiza o recorde do jogador
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        int recorde = prefs.getInt("recorde", 0);
        if (recordeScore != null) {
            recordeScore.setText("Record Score: " + recorde);
        }

        // Inicia ou retoma a música da tela inicial
        if (startMusic == null) {
            startMusic = MediaPlayer.create(this, R.raw.start); // Carrega música
            startMusic.setLooping(true);                        // Repetição contínua
            startMusic.start();
        } else if (!startMusic.isPlaying()) {
            startMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausa a música quando a tela for minimizada
        if (startMusic != null && startMusic.isPlaying()) {
            startMusic.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Encerra e libera a música se a tela for encerrada
        if (startMusic != null) {
            startMusic.stop();
            startMusic.release();
            startMusic = null;
        }
    }
}
