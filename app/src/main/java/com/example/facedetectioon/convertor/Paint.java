package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.facedetectioon.model.cache.CacheDataFace;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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
}
