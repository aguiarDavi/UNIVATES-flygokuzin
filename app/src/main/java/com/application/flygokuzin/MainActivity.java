package com.application.flygokuzin;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MediaPlayer gameMusic;
    private boolean somLigado = true; // Flag para controle do botão de som

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa sensores
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Inicia o jogo e a interface visual
        iniciarJogo();
    }

    // Inicializa a tela do jogo e os elementos da interface
    private void iniciarJogo() {
        gameView = new GameView(this);

        // Usa um FrameLayout para permitir sobrepor o botão de som à GameView
        FrameLayout layout = new FrameLayout(this);
        layout.addView(gameView);

        // Criação e configuração do botão de som
        ImageButton btnSom = new ImageButton(this);
        btnSom.setImageResource(R.drawable.btn_som_on);
        btnSom.setBackground(null);
        btnSom.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        btnSom.setAdjustViewBounds(true);
        btnSom.setContentDescription("Botão de Som");

        // Define o posicionamento do botão no canto superior direito
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(48), dpToPx(48));
        params.gravity = Gravity.TOP | Gravity.END;
        params.setMargins(0, dpToPx(24), dpToPx(8), 0);
        btnSom.setLayoutParams(params);

        // Listener do botão de som para pausar/iniciar a música
        btnSom.setOnClickListener(v -> {
            if (somLigado) {
                if (gameMusic != null && gameMusic.isPlaying()) {
                    gameMusic.pause();
                }
                btnSom.setImageResource(R.drawable.btn_som_off);
            } else {
                if (gameMusic != null) {
                    gameMusic.start();
                }
                btnSom.setImageResource(R.drawable.btn_som_on);
            }
            somLigado = !somLigado;
        });

        layout.addView(btnSom); // Adiciona botão ao layout
        setContentView(layout); // Aplica o layout final

        // Impede que a tela apague enquanto o jogo estiver rodando
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        gameView.resume(); // Inicia o loop do jogo
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        // Inicia a música do jogo se ainda não estiver rodando
        if (gameMusic == null) {
            gameMusic = MediaPlayer.create(this, R.raw.main);
            gameMusic.setLooping(true);
            gameMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) gameView.pause();
        sensorManager.unregisterListener(this);

        // Pausa a música se estiver tocando
        if (gameMusic != null && gameMusic.isPlaying()) {
            gameMusic.pause();
        }
    }

    // Recebe alterações do sensor (acelerômetro) e repassa ao personagem
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gameView != null) {
            gameView.setSensorX(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Não utilizado, mas necessário implementar
    }

    // Exibe a tela de Game Over e interrompe a música
    public void mostrarGameOver(int scoreFinal) {
        runOnUiThread(() -> {
            if (gameMusic != null) {
                gameMusic.stop();
                gameMusic.release();
                gameMusic = null;
            }

            if (!isFinishing()) {
                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                intent.putExtra("score", scoreFinal);
                startActivity(intent);
                finish();
            }
        });
    }

    // Toca o som de morte do personagem
    public void tocarSomDeMorte() {
        MediaPlayer deathSound = MediaPlayer.create(this, R.raw.death);
        deathSound.setOnCompletionListener(MediaPlayer::release);
        deathSound.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Encerra a música quando a atividade é parada
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusic.release();
            gameMusic = null;
        }
    }

    // Método auxiliar para converter dp em pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
