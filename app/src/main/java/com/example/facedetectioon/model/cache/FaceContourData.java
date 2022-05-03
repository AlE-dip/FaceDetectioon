package com.example.facedetectioon.model.cache;

import com.google.mlkit.vision.face.FaceContour;

public class FaceContourData{
    public FaceContour faceContour;
    public boolean close;

    public FaceContourData(FaceContour faceContour, boolean close) {
        this.faceContour = faceContour;
        this.close = close;
    }
}