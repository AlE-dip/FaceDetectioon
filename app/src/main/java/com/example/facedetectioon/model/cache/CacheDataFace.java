package com.example.facedetectioon.model.cache;

import android.graphics.Rect;

import com.google.mlkit.vision.face.FaceLandmark;

import java.util.ArrayList;

public class CacheDataFace {
    public Rect rect = null;
    public ArrayList<FaceContourData> faceContourDatas = null;
    public ArrayList<FaceLandmark> faceLandmarks = null;
}
