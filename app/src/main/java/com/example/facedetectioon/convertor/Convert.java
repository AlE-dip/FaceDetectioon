package com.example.facedetectioon.convertor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.facedetectioon.MainActivity;
import com.example.facedetectioon.model.CacheFilter;
import com.example.facedetectioon.model.CacheImage;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Convert {

    public static Bitmap readImageMatToBitmap(String path){
        Mat mat = Imgcodecs.imread(path, Imgcodecs.IMREAD_UNCHANGED);
        if(mat.cols() <= 0 || mat.rows() <= 0){
            mat = Mat.zeros(100, 100, CvType.CV_8U);
        } else {
            resize(mat, 3);
        }
        Log.d(MainActivity.FACE_DETECTION, mat.cols() + "  " + mat.rows() + path);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Bitmap matToBitmap(Mat mat){
        if(mat.cols() <= 0 || mat.rows() <= 0){
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Bitmap readImage(String path){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inMutable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        return bitmap;
    }

    public static Bitmap createBitmapFromMat(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static void resize(Mat mat, int size){
        if(size <= 0){
            size = 1;
        }
        Size size1 = new Size(mat.width()/size, mat.height()/size);
        Imgproc.resize(mat, mat, size1);
    }

    public static Bitmap changeImage(CacheFilter cacheFilter, Bitmap bitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        cacheFilter.getChangeImage().Filter(mat, cacheFilter.getConfigFilter());
        Bitmap bmNew = Convert.createBitmapFromMat(mat);
        return bmNew;
    }

    public static Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

}
