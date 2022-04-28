package com.example.facedetectioon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.facedetectioon.convertor.Filter;
import com.example.facedetectioon.model.CacheFilter;
import com.example.facedetectioon.model.ConfigFilter;
import com.example.facedetectioon.model.IChangeImage;
import com.example.facedetectioon.model.ListFilterAdapter;

import org.opencv.core.CvType;
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

    public ListFilterFragment(Context context,Bitmap bitmap, ImageView imageView) {
        this.context = context;
        this.bitmap = bitmap;
        this.imageView = imageView;
        createOperations();
    }

    private void createOperations() {
        cacheFilters = new ArrayList<>();

        cacheFilters.add(new CacheFilter(context.getString(R.string.default_image), null, new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                return;
            }
        }));

        ConfigFilter configFilter2 = new ConfigFilter();
        configFilter2.createSeekBar(80, 0, 255, context.getString(R.string.thresh));
        configFilter2.createSeekBar(255, 0, 255, context.getString(R.string.maxval));
        cacheFilters.add(new CacheFilter(context.getString(R.string.binary_image), configFilter2,new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(mat, mat, configFilter.seekBars.get(0).value, configFilter.seekBars.get(1).value, Imgproc.THRESH_BINARY);
            }
        }));

        cacheFilters.add(new CacheFilter(context.getString(R.string.gray_image), null, new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
            }
        }));

        ConfigFilter configFilter4 = new ConfigFilter();
        configFilter4.createSeekBar(100, 1, 200, context.getString(R.string.opacity));
        configFilter4.setSelected(5);
        configFilter4.createSelection(5, "5");
        configFilter4.createSelection(45, "45");
        configFilter4.createSelection(51, "51");
        cacheFilters.add(new CacheFilter(context.getString(R.string.gaussian_blur_image), configFilter4, new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                Imgproc.GaussianBlur(mat, mat, new Size(configFilter.selected, configFilter.selected), configFilter.seekBars.get(0).value / 10.0);
            }
        }));

        ConfigFilter configFilter5 = new ConfigFilter();
        configFilter5.createSeekBar(-8, -20, 0, context.getString(R.string.brightness));
        cacheFilters.add(new CacheFilter(context.getString(R.string.light_balance_gamma), configFilter5, new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                Filter.lightBalanceGamma(mat, configFilter.seekBars.get(0).value / 10.0 * -1);
            }
        }));

        ConfigFilter configFilter6 = new ConfigFilter();
        configFilter6.createSeekBar(2, 2, 50, context.getString(R.string.darkness));
        cacheFilters.add(new CacheFilter(context.getString(R.string.dark_image), configFilter6, new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                Filter.lightBalanceGamma(mat, configFilter.seekBars.get(0).value);
            }
        }));

        ConfigFilter configFilter7 = new ConfigFilter();
        configFilter7.setSelected(0);
        configFilter7.createSelection(2, context.getString(R.string.delete_blur));
        configFilter7.createSelection(1, context.getString(R.string.delete_green));
        configFilter7.createSelection(0, context.getString(R.string.delete_red));
        cacheFilters.add(new CacheFilter(context.getString(R.string.delete_color), configFilter7, new IChangeImage() {
            @Override
            public void Filter(Mat mat, ConfigFilter configFilter) {
                Filter.deleteColor(mat, configFilter.selected);
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

        listFilterAdapter = new ListFilterAdapter(cacheFilters, context, bitmap, imageView, rcListConfig);
        rcListFilter.setAdapter(listFilterAdapter);
        rcListFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }
}