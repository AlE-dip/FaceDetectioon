package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.List;

public class Paint {

    public static void drawRect(Bitmap bitmap, Rect rect){
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAlpha(0xA0); // the transparency
        paint.setColor(Color.RED); // color is red
        paint.setStyle(android.graphics.Paint.Style.STROKE); // stroke or fill or ...
        paint.setStrokeWidth(2); // the stroke width

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(rect, paint);
    }

    public static void drawLines(Bitmap bitmap, List<PointF> points, boolean closed){
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAlpha(0xA0); // the transparency
        paint.setColor(Color.RED);
        paint.setStyle(android.graphics.Paint.Style.STROKE); // stroke or fill or ...
        paint.setStrokeWidth(2); // the stroke width

        Canvas canvas = new Canvas(bitmap);
        if(points.size() > 1){
            PointF point = null;
            for(PointF pointF: points){
                if(point != null){
                    canvas.drawLine(point.x, point.y, pointF.x, pointF.y, paint);
                    point = pointF;
                    continue;
                }
                point = pointF;
            }
            if(closed){
                canvas.drawLine(point.x, point.y, points.get(0).x, points.get(0).y, paint);
            }
        } else {
            canvas.drawPoint(points.get(0).x, points.get(0).y, paint);
        }
    }

    public static void drawPoint(Bitmap bitmap, PointF pointF){
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAlpha(0xA0); // the transparency
        paint.setColor(Color.BLUE);
        paint.setStyle(android.graphics.Paint.Style.STROKE); // stroke or fill or ...
        paint.setStrokeWidth(9); // the stroke width

        Canvas canvas = new Canvas(bitmap);
        canvas.drawPoint(pointF.x, pointF.y, paint);
    }
}
