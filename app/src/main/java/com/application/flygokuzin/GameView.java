package com.application.flygokuzin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        obstacles = new ArrayList<>();

        post(() -> {
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
        });
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
        if (player != null) {
            player.update(sensorX);
        }

        Iterator<ObstacleActivity> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            ObstacleActivity obs = iterator.next();
            obs.update();
            if (obs.isOffScreen()) {
                iterator.remove();
            } else if (obs.colideCom(player)) {
                isPlaying = false;
            }
        }

        if (Math.random() < 0.02) {
            float x = (float) (Math.random() * getWidth());
            obstacles.add(new ObstacleActivity(getContext(), x, getHeight()));
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            if (player != null) {
                player.draw(canvas);
            }
            for (ObstacleActivity obs : obstacles) {
                obs.draw(canvas);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17); // ~60 FPS
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
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
