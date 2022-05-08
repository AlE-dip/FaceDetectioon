package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.facedetectioon.model.cache.CacheDataFace;
import com.example.facedetectioon.model.cache.CacheMat;
import com.example.facedetectioon.model.cache.FaceContourData;
import com.google.mlkit.vision.face.FaceLandmark;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Paint {

    public static void drawRect(Bitmap bitmap, Rect rect) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAlpha(0xA0); // the transparency
        paint.setColor(Color.RED); // color is red
        paint.setStyle(android.graphics.Paint.Style.STROKE); // stroke or fill or ...
        paint.setStrokeWidth(2); // the stroke width

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(rect, paint);
    }

    public static void drawRect(Mat mat, CacheDataFace cacheDataFace) {
        Rect rect = cacheDataFace.rect;
        Imgproc.rectangle(mat, new org.opencv.core.Rect(new Point(rect.left, rect.top), new Point(rect.right, rect.bottom)), new Scalar(255, 0, 0), 2);
    }

    public static void paintFace(Mat mat, CacheDataFace cacheDataFace) {
        if (cacheDataFace.rect != null) {
            Paint.drawRect(mat, FaceDetect.cacheDataFace);
            cacheDataFace.rect = null;
        }
        if (cacheDataFace.faceContourDatas != null) {
            for (FaceContourData faceContourData : cacheDataFace.faceContourDatas) {
                if (faceContourData.faceContour != null) {
                    Paint.drawLines(mat, faceContourData.faceContour.getPoints(), faceContourData.close);
                }
            }
            cacheDataFace.faceContourDatas = null;
        }
        if (cacheDataFace.faceLandmarks != null) {
            for (FaceLandmark faceLandmark : cacheDataFace.faceLandmarks) {
                if (faceLandmark != null) {
                    Paint.drawPoint(mat, faceLandmark.getPosition());
                }
            }
            cacheDataFace.faceLandmarks = null;
        }
    }

    public static void drawLines(Bitmap bitmap, List<PointF> points, boolean closed) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAlpha(0xA0); // the transparency
        paint.setColor(Color.RED);
        paint.setStyle(android.graphics.Paint.Style.STROKE); // stroke or fill or ...
        paint.setStrokeWidth(2); // the stroke width

        Canvas canvas = new Canvas(bitmap);
        if (points.size() > 1) {
            PointF point = null;
            for (PointF pointF : points) {
                if (point != null) {
                    canvas.drawLine(point.x, point.y, pointF.x, pointF.y, paint);
                    point = pointF;
                    continue;
                }
                point = pointF;
            }
            if (closed) {
                canvas.drawLine(point.x, point.y, points.get(0).x, points.get(0).y, paint);
            }
        } else {
            canvas.drawPoint(points.get(0).x, points.get(0).y, paint);
        }
    }

    public static void drawLines(Mat mat, List<PointF> points, boolean closed) {
        if (points.size() > 1) {
            PointF point = null;
            for (PointF pointF : points) {
                if (point != null) {
                    Imgproc.line(mat, new Point(point.x, point.y), new Point(pointF.x, pointF.y), new Scalar(255, 0, 0), 2);
                    point = pointF;
                    continue;
                }
                point = pointF;
            }
            if (closed) {
                Imgproc.line(mat, new Point(point.x, point.y), new Point(points.get(0).x, points.get(0).y), new Scalar(255, 0, 0), 2);
            }
        } else if (points.size() == 1){
            Imgproc.circle(mat, new Point(points.get(0).x, points.get(0).y), 2, new Scalar(255, 0, 0), 2);
        }
    }

    public static void drawPoint(Bitmap bitmap, PointF pointF) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAlpha(0xA0); // the transparency
        paint.setColor(Color.BLUE);
        paint.setStyle(android.graphics.Paint.Style.STROKE); // stroke or fill or ...
        paint.setStrokeWidth(9); // the stroke width

        Canvas canvas = new Canvas(bitmap);
        canvas.drawPoint(pointF.x, pointF.y, paint);
    }

    public static void drawPoint(Mat mat, PointF pointF) {
        Imgproc.circle(mat, new Point(pointF.x, pointF.y), 3, new Scalar(0, 0, 255), 3);
    }

    public static void zoomFacialPart(CacheMat cacheMat, int center, double size, FaceContourData... faceContourData){
        List<PointF> points = new ArrayList<>();
        for (FaceContourData f: faceContourData){
            if(f.faceContour != null){
                points.addAll(f.faceContour.getPoints());
            }
        }
        if(points.size() != 0){
            MatOfPoint matOfPoint = Convert.pointsToMatContour(points);
            Mat mask = Mat.zeros(cacheMat.mat.size(), cacheMat.mat.type());
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            contours.add(matOfPoint);
            PointF point = points.get(center);
            Point inZoom = new Point(point.x, point.y);
            ArrayList<MatOfPoint> zoomPoints = new ArrayList<>();
            zoomPoints.add(Convert.zoomPoint(matOfPoint, cacheMat.mat, size, inZoom));
            for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(mask, contours, i, new Scalar(255, 255, 255), Imgproc.FILLED);
                Core.bitwise_and(mask, cacheMat.mat, mask);
                Imgproc.drawContours(cacheMat.mat, zoomPoints, i, new Scalar(0, 0, 0), Imgproc.FILLED);
                mask = Convert.zoomAtPoint(mask, size, inZoom);
                Core.bitwise_or(cacheMat.mat, mask, cacheMat.mat);
            }
        }
    }
}
