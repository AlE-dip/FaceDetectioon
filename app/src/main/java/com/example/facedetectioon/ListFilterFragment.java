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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createOperations() {
        cacheFilters = new ArrayList<>();
        cacheFilters.add(new CacheFilter(context.getString(R.string.default_image), null, new IChangeImage() {
            @Override
            public void Filter(Mat mat) {
                return;
            }
        }));
        ConfigFilter configFilter2 = new ConfigFilter();
        cacheFilters.add(new CacheFilter(context.getString(R.string.binary_image), configFilter2,new IChangeImage() {
            @Override
            public void Filter(Mat mat) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(mat, mat, 127, 255, Imgproc.THRESH_BINARY);
            }
        }));
        ConfigFilter configFilter3 = new ConfigFilter();
        cacheFilters.add(new CacheFilter(context.getString(R.string.gray_image), configFilter3, new IChangeImage() {
            @Override
            public void Filter(Mat mat) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
            }
        }));
        ConfigFilter configFilter4 = new ConfigFilter();
        cacheFilters.add(new CacheFilter(context.getString(R.string.gaussian_blur_image), configFilter4, new IChangeImage() {
            @Override
            public void Filter(Mat mat) {
                Imgproc.GaussianBlur(mat, mat, new Size(45, 45), 0);
            }
        }));
        ConfigFilter configFilter5 = new ConfigFilter();
        cacheFilters.add(new CacheFilter(context.getString(R.string.light_balance_gamma), configFilter5, new IChangeImage() {
            @Override
            public void Filter(Mat mat) {
                Filter.lightBalanceGamma(mat, 2);
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