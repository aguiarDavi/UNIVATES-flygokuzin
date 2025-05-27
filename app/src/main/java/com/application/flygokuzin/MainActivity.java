package com.application.flygokuzin;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MediaPlayer gameMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gameMusic = MediaPlayer.create(this, R.raw.main);
        gameMusic.setLooping(true);
        gameMusic.start();

        iniciarJogo();
    }

    private void iniciarJogo() {
        gameView = new GameView(this);
        setContentView(gameView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gameView.resume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (gameMusic != null) {
            gameMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) gameView.pause();
        sensorManager.unregisterListener(this);
        if (gameMusic != null) {
            gameMusic.start();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gameView != null) {
            gameView.setSensorX(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void mostrarGameOver(int scoreFinal) {
        runOnUiThread(() -> {
            if (!isFinishing()) {
                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                intent.putExtra("score", scoreFinal);
                startActivity(intent);
                finish();
            }
        });
    }

    public void tocarSomDeMorte() {
        MediaPlayer deathSound = MediaPlayer.create(this, R.raw.death);
        deathSound.setOnCompletionListener(MediaPlayer::release);
        deathSound.start();
    }
}
