package com.example.facedetectioon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.facedetectioon.convertor.FaceDetect;
import com.example.facedetectioon.convertor.Filter;
import com.example.facedetectioon.convertor.TimeLine;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.ConfigFilter;
import com.example.facedetectioon.model.IChangeImage;
import com.example.facedetectioon.model.IDetectFace;
import com.example.facedetectioon.model.ListFilterAdapter;
import com.example.facedetectioon.model.cache.CacheMat;
import com.google.mlkit.vision.common.InputImage;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

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

    public ListFilterFragment(Context context, Bitmap bitmap, ImageView imageView) {
        this.context = context;
        this.bitmap = bitmap;
        this.imageView = imageView;
        this.faceDetect = new FaceDetect();
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
        }, null));

        ConfigFilter configFilter2 = new ConfigFilter();
        configFilter2.createSeekBar(80, 0, 255, context.getString(R.string.thresh));
        configFilter2.createSeekBar(255, 0, 255, context.getString(R.string.maxval));
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.binary_image), configFilter2, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Imgproc.cvtColor(mat, dst, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(dst, dst, configFilter.seekBars.get(0).value, configFilter.seekBars.get(1).value, Imgproc.THRESH_BINARY);
                return true;
            }
        }, null));

        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.gray_image), null, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Imgproc.cvtColor(mat, dst, Imgproc.COLOR_BGR2GRAY);
                return true;
            }
        }, null));

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
        }, null));

        ConfigFilter configFilter5 = new ConfigFilter();
        configFilter5.createSeekBar(-8, -20, 0, context.getString(R.string.brightness));
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.light_balance_gamma), configFilter5, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Filter.lightBalanceGamma(mat, dst, configFilter.seekBars.get(0).value / 10.0 * -1);
                return true;
            }
        }, null));

        ConfigFilter configFilter6 = new ConfigFilter();
        configFilter6.createSeekBar(2, 2, 50, context.getString(R.string.darkness));
        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_ALL, context.getString(R.string.dark_image), configFilter6, new IChangeImage() {
            @Override
            public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter) {
                Filter.lightBalanceGamma(mat, dst, configFilter.seekBars.get(0).value);
                return true;
            }
        }, null));

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

        cacheFilters.add(new CacheFilter(CacheFilter.TYPE_CAMERA, context.getString(R.string.draw_face), null, null, new IDetectFace() {
            @Override
            public void detectFacialPart(InputImage inputImage, CacheMat cacheMat, CacheFilter chooseCacheFilter, ImageView imageView, ImageProxy imageProxy, TimeLine timeLine) {
                faceDetect.drawFace(inputImage, cacheMat, chooseCacheFilter, imageView, imageProxy, timeLine);
            }
        }));

        cacheFilter.setCache(cacheFilters.get(6));
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
            listFilterAdapter = new ListFilterAdapter(CacheFilter.TYPE_IMAGE, cacheFilters, context, bitmap, imageView, rcListConfig);
        } else {
            listFilterAdapter = new ListFilterAdapter(CacheFilter.TYPE_CAMERA, cacheFilters, context, cacheMat, avatar, cacheFilter, imageView, rcListConfig);
        }
        rcListFilter.setAdapter(listFilterAdapter);
        rcListFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }
}