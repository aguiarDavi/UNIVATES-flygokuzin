package com.application.flygokuzin;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

public class ObstacleActivity {

    //private Bitmap obstacleFrame;
    private float x, y, width, height, speed;
    private float screenHeight;
    private Paint paint;

    private PlayerActivity player;

    //public ObstacleActivity(Context context, Bitmap bitmap, float startX, float screenHeight) {
    //    this.obstacleFrame = bitmap;
    //    this.x = startX;
    //    this.y = 0;
    //    this.speed = 10;
    //    this.screenHeight = screenHeight;
    //}

    public ObstacleActivity(Context context, float startX, float screenHeight) {
        this.x = startX;
        this.y = 0;
        this.width = 100;
        this.height = 100;
        this.speed = 10;
        this.screenHeight = screenHeight;

        paint = new Paint();
        paint.setColor(0xFFFF0000);
    }

    public void update(){
        y += speed;
    }

    public boolean isOffScreen(){
        return y > screenHeight;
    }

    public void draw( Canvas canvas){
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    //public void draw(Canvas canvas){
    //    canvas.drawBitmap(obstacleFrame, x, y, null);
    //}

    public boolean colideCom(PlayerActivity player){
        float px = player.getX();
        float py = player.getY();
        float pr = player.getRadius();

        RectF rect = new RectF(x, y, x + width, y + height);
        return rect.contains(px, py);
    }
}
