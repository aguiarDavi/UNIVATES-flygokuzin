package com.application.flygokuzin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        int score = getIntent().getIntExtra("score", 0);

        TextView scoreText = findViewById(R.id.textScoreFinal);
        TextView recordeText = findViewById(R.id.textRecorde);
        Button restartButton = findViewById(R.id.btnRestart);

        scoreText.setText("Score Final: " + score);

        SharedPreferences prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        int recorde = prefs.getInt("recorde", 0);

        recordeText.setText("Recorde: " + recorde);

        restartButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
