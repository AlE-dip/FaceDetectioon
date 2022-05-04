package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.camera.core.ImageProxy;

import com.example.facedetectioon.MainActivity;
import com.example.facedetectioon.model.cache.CacheDataFace;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.cache.CacheMat;
import com.example.facedetectioon.model.cache.FaceContourData;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceLandmark;

import org.opencv.core.Mat;

public class UtilFunction {

    private static int threadDone = 0;

    public synchronized static boolean isNullOnThread(Object o) {
        if (o == null) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized static Mat setMat(Mat o1, Mat o2) {
        o1 = o2;
        return o1;
    }

    //For faceDetection and CameraX
    public synchronized static boolean twoThreadDone() {
        threadDone++;
        //Log.d(MainActivity.FACE_DETECTION, "threadDone: " + threadDone);
        if (threadDone == 2) {
            threadDone = 0;
            return true;
        }
        return false;
    }

    public synchronized static void updateThreadDone() {
        threadDone++;
    }

    public synchronized static boolean isNonNull(Object o) {
        if (o != null) {
            return true;
        }
        return false;
    }

    public static void faceAndEffect(InputImage inputImage, CacheMat cacheMat, CacheFilter chooseCacheFilter, ImageProxy imageProxy, ImageView imageView, TimeLine timeLine) {
        if (chooseCacheFilter.getDetectFace() != null) {
            chooseCacheFilter.getDetectFace().detectFacialPart(inputImage, cacheMat, chooseCacheFilter, imageView, imageProxy, timeLine);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cacheMat.mat = Convert.imageProxyToMat(imageProxy);
                    if (twoThreadDone()) {
                        //Log.d(MainActivity.FACE_DETECTION, "threadDone: " + "camera");
                        Paint.paintFace(cacheMat.mat, FaceDetect.cacheDataFace);
                        //Convert.applyEffect(chooseCacheFilter, null, cacheMat.mat, null);
                        Bitmap bitmap = Convert.createBitmapFromMat(cacheMat.mat);
                        timeLine.setView(bitmap);
                        imageProxy.close();
                    }
                }
            }).start();
        } else {
            cacheMat.mat = Convert.imageProxyToMat(imageProxy);
            Convert.applyEffect(chooseCacheFilter, null, cacheMat.mat, imageView);
            imageProxy.close();
        }
    }

}
