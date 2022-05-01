package com.example.facedetectioon.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.opencv.core.Mat;

public interface IDetectFace {
    public void detectFacialPart(Bitmap bitmap, Mat mat, ConfigFilter configFilter, ImageView imageView);
}
