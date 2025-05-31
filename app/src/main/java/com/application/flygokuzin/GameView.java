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
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying = false;
    private SurfaceHolder holder;
    private PlayerActivity player;
    private List<ObstacleActivity> obstacles;
    private List<ObstacleActivity> obstaclesParaAdicionar;
    private List<ObstacleActivity> obstaclesParaRemover;
    private float sensorX;
    private int score = 0;
    private int recorde = 0;
    private long ultimoIncrementoScore = System.currentTimeMillis();
    private Paint textPaint;
    private boolean playerCriado = false;
    private boolean gameOverMostrado = false;
    private Canvas canvas;
    private ParallaxActivity layer1, layer2, layer3;


    public GameView(Context context) {
        super(context);
        holder = getHolder();
        obstacles = new ArrayList<>();

        obstaclesParaAdicionar = new ArrayList<>();
        obstaclesParaRemover = new ArrayList<>();

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setFakeBoldText(true);

        recorde = carregarRecorde();

        // Cria o player e o fundo após a view ser inicializada
        post(this::criarPlayer);
    }

    private void criarPlayer() {
        if (getWidth() == 0 || getHeight() == 0) {
            post(this::criarPlayer);
            return;
        }

        Bitmap raw = BitmapFactory.decodeResource(getResources(), R.drawable.bg_far2);
        Bitmap bgFar = Bitmap.createScaledBitmap(raw, getWidth(), getHeight(), false);
        //Bitmap bgFront = BitmapFactory.decodeResource(getResources(), R.drawable.bg_front);

        layer1 = new ParallaxActivity(bgFar, 1.0f, getHeight());
        //layer2 = new ParallaxActivity(bgMid, 2.0f, getHeight());
        //layer3 = new ParallaxActivity(bgFront, 3.0f, getHeight());

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
        if (layer1 != null) layer1.update();

        player.update(sensorX);

        synchronized (obstacles) {
            for (ObstacleActivity obs : obstacles) {
                obs.update();

                if (obs.isOffScreen()) {
                    obstaclesParaRemover.add(obs);
                } else if (obs.colideCom(player)) {
                    gameOverMostrado = true;
                    isPlaying = false;

                    if (score > recorde) {
                        salvarRecorde(score);
                        recorde = score;
                    }

                    post(() -> {
                        pause();
                        Context context = getContext();
                        if (context instanceof Activity) {
                            ((MainActivity) context).tocarSomDeMorte();
                            ((MainActivity) context).mostrarGameOver(score);
                        }
                    });

                    return;
                }
            }

            if (Math.random() < 0.02) {
                float x = (float) (Math.random() * getWidth());
                obstaclesParaAdicionar.add(new ObstacleActivity(getContext(), x, getHeight()));
            }

            obstacles.removeAll(obstaclesParaRemover);
            obstacles.addAll(obstaclesParaAdicionar);
            obstaclesParaRemover.clear();
            obstaclesParaAdicionar.clear();
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
            // canvas.drawColor(Color.WHITE);

            // Desenhar as camadas do fundo
            if (layer1 != null) layer1.draw(canvas);
            //if (layer2 != null) layer2.draw(canvas);
            //if (layer3 != null) layer3.draw(canvas);

            if (playerCriado && player != null) {
                player.draw(canvas);
            }

            synchronized (obstacles) {
                for (ObstacleActivity obs : obstacles) {
                    obs.draw(canvas);
                }
            }

            canvas.drawText("Score: " + score, 50, 100, textPaint);
            canvas.drawText("Recorde: " + recorde, 50, 180, textPaint);
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

    //LÓGICA DO RECORDE:
    private void salvarRecorde(int novoRecorde){
        Context context = getContext();
        context.getSharedPreferences("game_data",Context.MODE_PRIVATE)
                .edit()
                .putInt("recorde", novoRecorde)
                .apply();
    }

    private int carregarRecorde() {
        Context context = getContext();
        return context.getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .getInt("recorde",0);
    }
}
