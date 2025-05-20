package com.application.flygokuzin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class ObstacleActivity {

    private Bitmap obstacleFrame;
    private float x, y, speed;
    private float screenHeight;

    public ObstacleActivity(Context context, Bitmap bitmap, float startX, float screenHeight) {
        this.obstacleFrame = bitmap;
        this.x = startX;
        this.y = 0;
        this.speed = 10;
        this.screenHeight = screenHeight;
    }

    public void update(){
        y += speed;
    }

    public boolean isOffScreen(){
        return y > screenHeight;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(obstacleFrame, x, y, null);
    }

    public boolean colideCom(Player player){
        float px = player.getX();
        float py = player.hetY();
        float pr = player.getRadius();

        RectF rect = new RectF(x, y, x + obstacleFrame.getWidth(), y + obstacleFrame.getHeight());
        return rect.contains(px, py);
    }
}
