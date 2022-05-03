package com.example.facedetectioon.convertor;

import android.util.Log;

import com.example.facedetectioon.MainActivity;
import com.example.facedetectioon.model.cache.CacheDataFace;
import com.example.facedetectioon.model.cache.FaceContourData;
import com.google.mlkit.vision.face.FaceLandmark;

import org.opencv.core.Mat;

public class UtilFunction {

    private static int threadDone = 0;

    public synchronized static boolean isNullOnThread(Object o){
        if (o == null){
            return true;
        } else {
            return false;
        }
    }

    public synchronized static Mat setMat(Mat o1, Mat o2){
        o1 = o2;
        return o1;
    }

    //For faceDetection and CameraX
    public synchronized static boolean twoThreadDone(){
        threadDone++;
        if(threadDone == 2){
            threadDone = 0;
            return true;
        }
        return false;
    }

    public synchronized static void updateThreadDone(){
        threadDone++;
    }

    public synchronized static boolean isNonNull(Object o){
        if(o != null){
            return true;
        }
        return false;
    }

    public synchronized static void paintFace(Mat mat, CacheDataFace cacheDataFace){
        if(cacheDataFace.rect != null){
            Paint.drawRect(mat, FaceDetect.cacheDataFace);
            cacheDataFace.rect = null;
        }
        if(cacheDataFace.faceContourDatas != null){
            for (FaceContourData faceContourData: cacheDataFace.faceContourDatas){
                if(faceContourData.faceContour != null){
                    Paint.drawLines(mat, faceContourData.faceContour.getPoints(), faceContourData.close);
                }
            }
            cacheDataFace.faceContourDatas = null;
        }
        if(cacheDataFace.faceLandmarks != null){
            for (FaceLandmark faceLandmark: cacheDataFace.faceLandmarks){
                if(faceLandmark != null){
                    Paint.drawPoint(mat, faceLandmark.getPosition());
                }
            }
        }
    }

}
