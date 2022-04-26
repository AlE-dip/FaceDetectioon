package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.example.facedetectioon.MainActivity;
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

import java.util.List;

public class FaceDetect {

    private FaceDetector detector;

    public FaceDetect() {
        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        detector = FaceDetection.getClient(highAccuracyOpts);
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
}
