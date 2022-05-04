package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.example.facedetectioon.MainActivity;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.IChangeImage;
import com.example.facedetectioon.model.cache.CacheDataFace;
import com.example.facedetectioon.model.cache.CacheMat;
import com.example.facedetectioon.model.cache.FaceContourData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FaceDetect {

    private FaceDetector detector;
    public static CacheDataFace cacheDataFace;

    public FaceDetect() {
        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        detector = FaceDetection.getClient(highAccuracyOpts);
        cacheDataFace = new CacheDataFace();
    }

    public void getBoundEye(Bitmap bitmap, Mat mat, IChangeImage changeImage, ImageView imageView){
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        detector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {
                        for (Face face: faces){

                            FaceContour leftEyeFC = face.getContour(FaceContour.LEFT_EYE);
                            if(leftEyeFC != null){
                                List<PointF> leftEyeContour = leftEyeFC.getPoints();

                                org.opencv.core.Rect lRect = createRect(leftEyeContour, mat);

//                                MatOfPoint2f src = new MatOfPoint2f(
//                                        new Point(lRect.x, lRect.y),
//                                        new Point(lRect.x + lRect.width, lRect.y),
//                                        new Point(lRect.x, lRect.y + lRect.height),
//                                        new Point(lRect.x + lRect.width, lRect.y + lRect.height));
//
//                                MatOfPoint2f dst = new MatOfPoint2f(
//                                        new Point(lRect.x - 4, lRect.y - 4),
//                                        new Point(lRect.x + lRect.width + 4 * 2, lRect.y - 4),
//                                        new Point(lRect.x - 4, lRect.y + lRect.height + 4 * 2),
//                                        new Point(lRect.x + lRect.width + 8, lRect.y + lRect.height + 8));

                                MatOfPoint2f src = new MatOfPoint2f(
                                        new Point(56, 65),
                                        new Point(368, 52),
                                        new Point(28, 387),
                                        new Point(389, 390));

                                MatOfPoint2f dst = new MatOfPoint2f(
                                        new Point(0, 0),
                                        new Point(300, 0),
                                        new Point(0, 300),
                                        new Point(300, 300));

                                Mat mP = Imgproc.getPerspectiveTransform(src, dst);
                                Imgproc.warpPerspective(mat, mat, mP, new Size(5, 5));

//                               Mat lEye = new Mat(mat, lRect);

                                Bitmap bmNew = Convert.createBitmapFromMat(mat);
                                imageView.setImageBitmap(bmNew);
                            }

                            FaceContour rightEyeFC = face.getContour(FaceContour.RIGHT_EYE);
                            if(rightEyeFC != null){
                                List<PointF> rightEyeContour = rightEyeFC.getPoints();
//                                Paint.drawLines(bmNew, rightEyeContour, true);
                            }

                            FaceLandmark leftEyeLam = face.getLandmark(FaceLandmark.LEFT_EYE);
                            if(leftEyeLam != null){
                                PointF leftEye = leftEyeLam.getPosition();

                            }

                            FaceLandmark righEyeLam = face.getLandmark(FaceLandmark.RIGHT_EYE);
                            if(righEyeLam != null){
                                PointF rightEye = righEyeLam.getPosition();
                            }

//                            imageView.setImageBitmap(bmNew);
                        }
                    }
                });
    }

    private org.opencv.core.Rect createRect(List<PointF> points, Mat mat){
        int ran = 20;

        PointF pointF = points.get(0);
        org.opencv.core.Rect rect = new org.opencv.core.Rect((int) pointF.x,(int) pointF.y, 0, 0);

        for(int i = 1; i < points.size(); i++){
            PointF point = points.get(i);
            if(point.x < rect.x){
                rect.x = (int) point.x;
            }
            if(point.x - rect.x > rect.width){
                rect.width = (int) point.x - rect.x;
            }
            if(point.y < rect.y){
                rect.y = (int) point.y;
            }
            if(point.y - rect.y > rect.height){
                rect.height = (int) point.y - rect.y;
            }
        }
        if(rect.x - ran < 0){
            rect.x = 0;
        } else {
            rect.x = rect.x - ran;
        }
        if(rect.y - ran < 0){
            rect.y = 0;
        } else {
            rect.y = rect.y - ran;
        }
        if(rect.width + ran * 2 > mat.cols()){
            rect.width = mat.cols();
        } else {
            rect.width = rect.width + ran * 2;
        }
        if(rect.height + ran * 2 > mat.rows()){
            rect.height = mat.rows();
        } else {
            rect.height = rect.height + ran * 2;
        }
        return rect;
    }

    public void drawFace(Bitmap bitmap, ImageView imageView) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        Task<List<Face>> result = detector.process(inputImage)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {

                                for (Face face : faces) {
                                    Rect bounds = face.getBoundingBox();
                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                    Paint.drawRect(bitmap, bounds);

                                    // If contour detection was enabled:
                                    List<PointF> leftEyeContour = face.getContour(FaceContour.LEFT_EYE).getPoints();
                                    List<PointF> rightEyeContour = face.getContour(FaceContour.RIGHT_EYE).getPoints();

                                    List<PointF> faceContour = face.getContour(FaceContour.FACE).getPoints();

                                    List<PointF> leftEyebrowBottomContour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM).getPoints();
                                    List<PointF> leftEyebrowTopContour = face.getContour(FaceContour.LEFT_EYEBROW_TOP).getPoints();
                                    List<PointF> rightEyebrowBottomContour = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM).getPoints();
                                    List<PointF> rightEyebrowTopContour = face.getContour(FaceContour.RIGHT_EYEBROW_TOP).getPoints();

                                    List<PointF> upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM).getPoints();
                                    List<PointF> upperLipTopContour = face.getContour(FaceContour.UPPER_LIP_TOP).getPoints();
                                    List<PointF> lowerLipBottomContour = face.getContour(FaceContour.LOWER_LIP_BOTTOM).getPoints();
                                    List<PointF> lowerLipTopContour = face.getContour(FaceContour.LOWER_LIP_TOP).getPoints();

                                    List<PointF> noseBottomContour = face.getContour(FaceContour.NOSE_BOTTOM).getPoints();
                                    List<PointF> noseBridgeContour = face.getContour(FaceContour.NOSE_BRIDGE).getPoints();

                                    List<PointF> leftCheekContour = face.getContour(FaceContour.LEFT_CHEEK).getPoints();
                                    List<PointF> rightCheekContour = face.getContour(FaceContour.RIGHT_CHEEK).getPoints();

                                    Paint.drawLines(bitmap, leftEyeContour, true);
                                    Paint.drawLines(bitmap, rightEyeContour, true);

                                    Paint.drawLines(bitmap, faceContour, true);

                                    Paint.drawLines(bitmap, leftEyebrowBottomContour, false);
                                    Paint.drawLines(bitmap, leftEyebrowTopContour, false);
                                    Paint.drawLines(bitmap, rightEyebrowBottomContour, false);
                                    Paint.drawLines(bitmap, rightEyebrowTopContour, false);

                                    Paint.drawLines(bitmap, upperLipBottomContour, false);
                                    Paint.drawLines(bitmap, upperLipTopContour, false);
                                    Paint.drawLines(bitmap, lowerLipBottomContour, false);
                                    Paint.drawLines(bitmap, lowerLipTopContour, false);

                                    Paint.drawLines(bitmap, noseBottomContour, false);
                                    Paint.drawLines(bitmap, noseBridgeContour, false);

                                    Paint.drawLines(bitmap, leftCheekContour, false);
                                    Paint.drawLines(bitmap, rightCheekContour, false);

                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                    // nose available):
                                    PointF leftEar = face.getLandmark(FaceLandmark.LEFT_EAR).getPosition();
                                    PointF rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR).getPosition();

                                    PointF leftEye = face.getLandmark(FaceLandmark.LEFT_EYE).getPosition();
                                    PointF rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE).getPosition();

                                    PointF rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK).getPosition();
                                    PointF leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK).getPosition();

                                    PointF mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM).getPosition();
                                    PointF mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT).getPosition();
                                    PointF mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT).getPosition();

                                    PointF noseBase = face.getLandmark(FaceLandmark.NOSE_BASE).getPosition();

                                    Paint.drawPoint(bitmap, leftEar);
                                    Paint.drawPoint(bitmap, rightEar);

                                    Paint.drawPoint(bitmap, leftEye);
                                    Paint.drawPoint(bitmap, rightEye);

                                    Paint.drawPoint(bitmap, leftCheek);
                                    Paint.drawPoint(bitmap, rightCheek);

                                    Paint.drawPoint(bitmap, mouthBottom);
                                    Paint.drawPoint(bitmap, mouthLeft);
                                    Paint.drawPoint(bitmap, mouthRight);

                                    Paint.drawPoint(bitmap, noseBase);

                                    imageView.setImageBitmap(bitmap);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(MainActivity.FACE_DETECTION, "Can not detect the face!");
                            }
                        });
    }

    public void drawFace(InputImage inputImage, CacheMat cacheMat, CacheFilter chooseCacheFilter, ImageView imageView, ImageProxy imageProxy, TimeLine timeLine) {
        Task<List<Face>> result = detector.process(inputImage)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {

                                for (Face face : faces) {
                                    Rect bounds = face.getBoundingBox();
                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                    cacheDataFace.rect = bounds;

                                    cacheDataFace.faceContourDatas = new ArrayList<>();
                                    ArrayList<FaceContourData> faceContourDatas = cacheDataFace.faceContourDatas;
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.LEFT_EYE), true));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.RIGHT_EYE), true));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.LEFT_EYEBROW_TOP), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.RIGHT_EYEBROW_TOP), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.UPPER_LIP_BOTTOM), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.UPPER_LIP_TOP), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.LOWER_LIP_BOTTOM), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.LOWER_LIP_TOP), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.NOSE_BOTTOM), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.NOSE_BRIDGE), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.LEFT_CHEEK), false));
                                    faceContourDatas.add(new FaceContourData(face.getContour(FaceContour.RIGHT_CHEEK), false));

                                    cacheDataFace.faceLandmarks = new ArrayList<>();
                                    ArrayList<FaceLandmark> faceLandmarks = cacheDataFace.faceLandmarks;
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.LEFT_EAR));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.RIGHT_EAR));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.LEFT_EYE));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.RIGHT_EYE));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.RIGHT_CHEEK));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.LEFT_CHEEK));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.MOUTH_BOTTOM));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.MOUTH_RIGHT));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.MOUTH_LEFT));
                                    faceLandmarks.add(face.getLandmark(FaceLandmark.NOSE_BASE));
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(UtilFunction.twoThreadDone()){
                                            //Log.d(MainActivity.FACE_DETECTION, "threadDone: " + "face");
                                            Paint.paintFace(cacheMat.mat, cacheDataFace);
                                            //Convert.applyEffect(chooseCacheFilter, null, mat, null);
                                            Bitmap bitmap = Convert.createBitmapFromMat(cacheMat.mat);
                                            timeLine.setView(bitmap);
                                            imageProxy.close();
                                        }
                                    }
                                }).start();
                                //imageProxy.close();
                                //Log.d(MainActivity.FACE_DETECTION, "Time: " + ((System.currentTimeMillis() - time)));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(MainActivity.FACE_DETECTION, "Can not detect the face!");
                                e.printStackTrace();
                                imageProxy.close();
                            }
                        });
    }

}
