package com.example.facedetectioon.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.camera.core.ImageProxy;

import com.example.facedetectioon.convertor.TimeLine;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.cache.CacheMat;
import com.google.mlkit.vision.common.InputImage;

import org.opencv.core.Mat;

public interface IDetectFace {
    public void detectFacialPart(InputImage inputImage, CacheMat cacheMat, CacheFilter chooseCacheFilter, ImageView imageView, ImageProxy imageProxy, TimeLine timeLine);
}
