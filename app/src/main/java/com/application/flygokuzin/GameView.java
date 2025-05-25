package com.application.flygokuzin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying = false;
    private SurfaceHolder holder;

    private PlayerActivity player;
    private List<ObstacleActivity> obstacles;
    private float sensorX;

    private int score = 0;
    private long ultimoIncrementoScore = System.currentTimeMillis();
    private Paint textPaint;

    private boolean playerCriado = false;
    private boolean gameOverMostrado = false;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        obstacles = new ArrayList<>();

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setFakeBoldText(true);

        post(this::criarPlayer);
    }

    private void criarPlayer() {
        if (getWidth() == 0 || getHeight() == 0) {
            post(this::criarPlayer);
            return;
        }

        int centerX = getWidth() / 2;
        int centerY = getHeight() - 300;

        Bitmap[] direita = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.goku1_d),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku2_d),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku3_d)
        };

        Bitmap[] esquerda = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.goku1_e),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku2_e),
                BitmapFactory.decodeResource(getResources(), R.drawable.goku3_e)
        };

        player = new PlayerActivity(centerX, centerY, 60, direita, esquerda);
        player.setCanvasWidth(getWidth());
        playerCriado = true;
    }

    public void setSensorX(float sensorX) {
        this.sensorX = sensorX;
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        if (!playerCriado || gameOverMostrado) return;

        long agora = System.currentTimeMillis();
        if (agora - ultimoIncrementoScore >= 100) {
            score++;
            ultimoIncrementoScore = agora;

            if (score % 100 == 0) {
                aumentarDificuldade();
            }
        }

        player.update(sensorX);

        Iterator<ObstacleActivity> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            ObstacleActivity obs = iterator.next();
            obs.update();
            if (obs.isOffScreen()) {
                iterator.remove();
            } else if (obs.colideCom(player)) {
                gameOverMostrado = true;
                isPlaying = false;

                post(() -> {
                    pause();  // Para a thread do jogo

                    Context context = getContext();
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        if (!activity.isFinishing()) {
                            ((MainActivity) activity).mostrarGameOver(score);
                        }
                    }
                });

                return;
            }
        }

        if (Math.random() < 0.02) {
            float x = (float) (Math.random() * getWidth());
            obstacles.add(new ObstacleActivity(getContext(), x, getHeight()));
        }
    }

    private void aumentarDificuldade() {
        for (ObstacleActivity obs : obstacles) {
            obs.aumentarVelocidade(1.05f);
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            if (playerCriado && player != null) {
                player.draw(canvas);
            }

            for (ObstacleActivity obs : obstacles) {
                obs.draw(canvas);
            }

            canvas.drawText("Score: " + score, 50, 100, textPaint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            if (gameThread != null && gameThread.isAlive()) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
