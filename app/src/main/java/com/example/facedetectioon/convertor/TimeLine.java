package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class TimeLine {
    public long time;
    public ImageView imageView;

    public TimeLine(ImageView imageView) {
        this.time = System.currentTimeMillis();
        this.imageView = imageView;
    }

    public synchronized void setView(Bitmap bitmap){
        if(time < System.currentTimeMillis()){
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                    setTime(System.currentTimeMillis());
                }
            });
        }
    }

    public synchronized void setTime(long time){
        if(this.time < time){
            this.time = time;
        }
    }
}
