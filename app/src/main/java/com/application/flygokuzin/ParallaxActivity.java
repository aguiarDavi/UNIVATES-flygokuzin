package com.application.flygokuzin;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ParallaxActivity {

    private Bitmap image;
    private float y;
    private float speed;
    private int alturaTela;

    public ParallaxActivity(Bitmap bitmap, float speed, int alturaTela){
        this.image = bitmap;
        this.speed = speed;
        this.y = 0;
        this.alturaTela = alturaTela;
    }

    public void update(){
        y += speed;
        if(y >= alturaTela){
            y = 0;
        }
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image, 0, y, null);
        canvas.drawBitmap(image, 0, y - alturaTela, null);
    }
}
