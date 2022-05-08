package com.example.facedetectioon.convertor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.example.facedetectioon.MainActivity;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.cache.CacheMat;
import com.google.mlkit.vision.common.InputImage;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.List;

public class Convert {

    public static Bitmap readImageMatToBitmap(String path) {
        Mat mat = Imgcodecs.imread(path, Imgcodecs.IMREAD_UNCHANGED);
        if (mat.cols() <= 0 || mat.rows() <= 0) {
            mat = Mat.zeros(100, 100, CvType.CV_8U);
        } else {
            resize(mat, 3);
        }
        Log.d(MainActivity.FACE_DETECTION, mat.cols() + "  " + mat.rows() + path);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Bitmap matToBitmap(Mat mat) {
        if (mat.cols() <= 0 || mat.rows() <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Bitmap readImage(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inMutable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        return bitmap;
    }

    public static Bitmap createBitmapFromMat(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static void resize(Mat mat, int size) {
        if (size <= 0) {
            size = 1;
        }
        Size size1 = new Size(mat.width() / size, mat.height() / size);
        Imgproc.resize(mat, mat, size1);
    }

    public static Mat zoomAtPoint(Mat mat, double size, Point point) {
        Size s = mat.size();
        if (size <= 0) {
            size = 1;
        }
        Size size1 = new Size(mat.width() * size, mat.height() * size);
        Imgproc.resize(mat, mat, size1);
        int inX = (int) (point.x * size - point.x);
        int inY = (int) (point.y * size - point.y);
        if (inX < 0) inX = 0;
        if (inY < 0) inY = 0;
        if (inX + s.width > mat.cols()) inX = (int) (inX - (s.width - (mat.cols() - inX)));
        if (inY + s.height > mat.rows()) inY = (int) (inY - (s.height - (mat.rows() - inY)));
        Rect rect = new Rect(inX, inY, (int) s.width, (int) s.height);
        Mat dst = new Mat(mat, rect);
        return dst;
    }

    public static MatOfPoint zoomPoint(MatOfPoint matOfPoint, Mat mat, double size, Point point) {
        MatOfPoint mop = new MatOfPoint();
        Point[] points = matOfPoint.toArray();
        for (Point p: points){
            double inX = point.x - ((point.x - p.x) * size);
            double inY = point.y - ((point.y - p.y) * size);
            if (inX < 0) inX = 0;
            if (inX > mat.cols()) inX = mat.cols();
            if (inY < 0) inY = 0;
            if (inY > mat.rows()) inY = mat.rows();
            p.x = inX;
            p.y = inY;
        }
        mop.fromArray(points);
        return mop;
    }

    public static MatOfPoint pointsToMatContour(List<PointF> points){
        Point[] ps = new Point[points.size()];
        for (int i = 0; i < points.size(); i++){
            PointF p = points.get(i);
            ps[i] = new Point(p.x, p.y);
        }
        MatOfPoint matOfPoint = new MatOfPoint();
        matOfPoint.fromArray(ps);
        return matOfPoint;
    }

    public static Mat imageProxyToMat(@NonNull ImageProxy imageProxy) {
        ByteBuffer buffer;
        int rowStride;
        int pixelStride;
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();
        int offset = 0;

        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
        byte[] data = new byte[imageProxy.getWidth() * imageProxy.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        for (int i = 0; i < planes.length; i++) {
            buffer = planes[i].getBuffer();
            rowStride = planes[i].getRowStride();
            pixelStride = planes[i].getPixelStride();

            int w = (i == 0) ? width : width / 2;
            int h = (i == 0) ? height : height / 2;
            for (int row = 0; row < h; row++) {
                int bytesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
                if (pixelStride == bytesPerPixel) {
                    int length = w * bytesPerPixel;
                    buffer.get(data, offset, length);

                    if (h - row != 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                    offset += length;
                } else {


                    if (h - row == 1) {
                        buffer.get(rowData, 0, width - pixelStride + 1);
                    } else {
                        buffer.get(rowData, 0, rowStride);
                    }

                    for (int col = 0; col < w; col++) {
                        data[offset++] = rowData[col * pixelStride];
                    }
                }
            }
        }

        Mat mat = new Mat(height + height / 2, width, CvType.CV_8UC1);
        mat.put(0, 0, data);

        Mat rgbOut = new Mat(imageProxy.getHeight(), imageProxy.getWidth(), CvType.CV_8UC3);
        Imgproc.cvtColor(mat, rgbOut, Imgproc.COLOR_YUV2RGB_I420);

        Core.rotate(rgbOut, rgbOut, Core.ROTATE_90_CLOCKWISE);

        return rgbOut;
    }

    public static void resize(Mat mat, Size size) {
        Imgproc.resize(mat, mat, size);
    }

    public static Bitmap applyEffect(CacheFilter cacheFilter, Bitmap bitmap, Mat mat, ImageView imageView) {
        if(bitmap != null){
            mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
        }  else if(mat == null) {
            mat  = Mat.zeros(new Size(100, 100), CvType.CV_8UC3);
        }
        Mat dst = new Mat(mat.size(), mat.type());
        if (cacheFilter.getChangeImage() != null) {
            Bitmap bmNew;
            if(cacheFilter.getChangeImage().Filter(mat, dst, cacheFilter.getConfigFilter())){
                bmNew = Convert.createBitmapFromMat(dst);
            } else {
                bmNew = Convert.createBitmapFromMat(mat);
            }
            if(imageView != null){
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bmNew);
                    }
                });
            }
            return bmNew;
        }
        return null;
    }

    public static Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

}
