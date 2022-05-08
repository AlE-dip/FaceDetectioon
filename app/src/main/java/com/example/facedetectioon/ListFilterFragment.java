package com.example.facedetectioon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.facedetectioon.convertor.Convert;
import com.example.facedetectioon.convertor.FaceDetect;
import com.example.facedetectioon.convertor.Filter;
import com.example.facedetectioon.convertor.Paint;
import com.example.facedetectioon.convertor.TimeLine;
import com.example.facedetectioon.model.IOperation;
import com.example.facedetectioon.model.cache.CacheDataFace;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.ConfigFilter;
import com.example.facedetectioon.model.IChangeImage;
import com.example.facedetectioon.model.IDetectFace;
import com.example.facedetectioon.model.ListFilterAdapter;
import com.example.facedetectioon.model.cache.CacheMat;
import com.example.facedetectioon.model.cache.FaceContourData;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceLandmark;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TickMeter;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ListFilterFragment extends Fragment {

    private Context context;
    private ArrayList<CacheFilter> cacheFilters;
    private Bitmap bitmap;
    private RecyclerView rcListFilter, rcListConfig;
    private ListFilterAdapter listFilterAdapter;
    private ImageView imageView;
    private CacheMat cacheMat;
    private CacheFilter cacheFilter;
    private Mat avatar;
    private FaceDetect faceDetect;

    public ListFilterFragment(Context context, Bitmap bitmap, CacheFilter cacheFilter, ImageView imageView) {
        this.context = context;
        this.bitmap = bitmap;
        this.imageView = imageView;
        this.faceDetect = new FaceDetect();
        this.cacheFilter = cacheFilter;
        createOperations();
    }

    public ListFilterFragment(Context context, CacheMat cacheMat, Mat avatar, CacheFilter cacheFilter, ImageView imageView) {
        this.context = context;
        this.cacheMat = cacheMat;
        this.imageView = imageView;
        this.cacheFilter = cacheFilter;
        this.avatar = avatar;
        this.faceDetect = new FaceDetect();
        createOperations();
    }

    private void createOperations() {
        cacheFilters = new ArrayList<>();

        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.default_image), null, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                return false;
            }
        }, null, null));

        cacheFilter.setCache(cacheFilters.get(0));

        ConfigFilter configFilter2 = new ConfigFilter();
        configFilter2.createSeekBar(80, 0, 255, context.getString(R.string.thresh));
        configFilter2.createSeekBar(255, 0, 255, context.getString(R.string.maxval));
        configFilter2.setSelected(Imgproc.THRESH_BINARY);
        configFilter2.createSelection(Imgproc.THRESH_BINARY, "THRESH_BINARY");
        configFilter2.createSelection(Imgproc.THRESH_BINARY_INV, "THRESH_BINARY_INV");
        configFilter2.createSelection(Imgproc.THRESH_TRUNC, "THRESH_TRUNC");
        configFilter2.createSelection(Imgproc.THRESH_MASK, "THRESH_MASK");
        configFilter2.createSelection(Imgproc.THRESH_OTSU, "THRESH_OTSU");
        configFilter2.createSelection(Imgproc.THRESH_TOZERO, "THRESH_TOZERO");
        configFilter2.createSelection(Imgproc.THRESH_TOZERO_INV, "THRESH_TOZERO_INV");
        configFilter2.createSelection(Imgproc.THRESH_TRIANGLE, "THRESH_TRIANGLE");
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.binary_image), configFilter2, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Imgproc.cvtColor(mat, dst, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(dst, dst, configFilter.seekBars.get(0).value, configFilter.seekBars.get(1).value, configFilter.selected);
                return true;
            }
        }, null, null));

        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.gray_image), null, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Imgproc.cvtColor(mat, dst, Imgproc.COLOR_BGR2GRAY);
                return true;
            }
        }, null, null));

        ConfigFilter configFilter4 = new ConfigFilter();
        configFilter4.createSeekBar(100, 1, 200, context.getString(R.string.opacity));
        configFilter4.setSelected(5);
        configFilter4.createSelection(5, "5");
        configFilter4.createSelection(45, "45");
        configFilter4.createSelection(51, "51");
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.gaussian_blur_image), configFilter4, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Imgproc.GaussianBlur(mat, dst, new Size(configFilter.selected, configFilter.selected), configFilter.seekBars.get(0).value / 10.0);
                return true;
            }
        }, null, null));

        ConfigFilter configFilter5 = new ConfigFilter();
        configFilter5.createSeekBar(-8, -20, 0, context.getString(R.string.brightness));
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.light_balance_gamma), configFilter5, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Filter.lightBalanceGamma(mat, dst, configFilter.seekBars.get(0).value / 10.0 * -1);
                return true;
            }
        }, null, null));

        ConfigFilter configFilter6 = new ConfigFilter();
        configFilter6.createSeekBar(2, 2, 50, context.getString(R.string.darkness));
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.dark_image), configFilter6, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Filter.lightBalanceGamma(mat, dst, configFilter.seekBars.get(0).value);
                return true;
            }
        }, null, null));

//        ConfigFilter configFilter7 = new ConfigFilter();
//        configFilter7.setSelected(0);
//        configFilter7.createSelection(2, context.getString(R.string.delete_blur));
//        configFilter7.createSelection(1, context.getString(R.string.delete_green));
//        configFilter7.createSelection(0, context.getString(R.string.delete_red));
//        cacheFilters.add(new CacheFilter(context.getString(R.string.delete_color), configFilter7, new IChangeImage() {
//            @Override
//            public void Filter(Mat mat, ConfigFilter configFilter) {
//                Filter.deleteColor(mat, configFilter.selected);
//            }
//        }, null));

        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_CAMERA, context.getString(R.string.draw_face), null, null,
                new IDetectFace() {
                    @Override
                    public void detectFacialPart(InputImage inputImage, CacheMat cacheMat, CacheFilter chooseCacheFilter, ImageView imageView, ImageProxy imageProxy, TimeLine timeLine) {
                        faceDetect.drawFace(inputImage, cacheMat, chooseCacheFilter, imageView, imageProxy, timeLine);
                    }
                },
                new IOperation() {
                    @Override
                    public void operate(CacheMat cacheMat, CacheDataFace cacheDataFace, ConfigFilter configFilter) {
                        Paint.paintFace(cacheMat.mat, cacheDataFace);
                    }
                }));

        ConfigFilter configFilter7 = new ConfigFilter();
        configFilter7.createSeekBar(2, 1,4,  context.getString(R.string.size_zoom));
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_CAMERA, context.getString(R.string.edit_eye), configFilter7, null,
                new IDetectFace() {
                    @Override
                    public void detectFacialPart(InputImage inputImage, CacheMat cacheMat, CacheFilter chooseCacheFilter, ImageView imageView, ImageProxy imageProxy, TimeLine timeLine) {
                        faceDetect.zoonFace(inputImage, cacheMat, chooseCacheFilter, imageView, imageProxy, timeLine);
                    }
                },
                new IOperation() {
                    @Override
                    public void operate(CacheMat cacheMat, CacheDataFace cacheDataFace, ConfigFilter configFilter) {
                        if(cacheDataFace.faceContourDatas == null) return;
                        FaceContourData lEye = cacheDataFace.faceContourDatas.get(0);
                        FaceContourData rEye = cacheDataFace.faceContourDatas.get(1);
                        FaceContourData upLip = cacheDataFace.faceContourDatas.get(2);
                        FaceContourData lowLip = cacheDataFace.faceContourDatas.get(3);

                        Paint.zoomFacialPart(cacheMat, 8, configFilter.seekBars.get(0).value, lEye);
                        Paint.zoomFacialPart(cacheMat, 0, configFilter.seekBars.get(0).value, rEye);
                        Paint.zoomFacialPart(cacheMat, 5, configFilter.seekBars.get(0).value, upLip, lowLip);

                        cacheDataFace.faceContourDatas = null;
                    }
                }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcListFilter = view.findViewById(R.id.rcListFilter);
        rcListConfig = view.findViewById(R.id.rcListConfig);

        if (bitmap != null) {
            listFilterAdapter = new ListFilterAdapter(CacheFilter.TYPE_IMAGE, cacheFilters, cacheFilter, context, bitmap, imageView, rcListConfig);
        } else {
            listFilterAdapter = new ListFilterAdapter(CacheFilter.TYPE_CAMERA, cacheFilters, context, cacheMat, avatar, cacheFilter, imageView, rcListConfig);
        }
        rcListFilter.setAdapter(listFilterAdapter);
        rcListFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }
}