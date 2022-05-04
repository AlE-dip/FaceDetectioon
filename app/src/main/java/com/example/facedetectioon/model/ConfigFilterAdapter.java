package com.example.facedetectioon.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facedetectioon.MainActivity;
import com.example.facedetectioon.R;
import com.example.facedetectioon.convertor.Convert;
import com.example.facedetectioon.convertor.Filter;
import com.example.facedetectioon.model.cache.CacheMat;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ConfigFilterAdapter extends RecyclerView.Adapter<ConfigFilterAdapter.ViewHolder> {

    private Context context;
    private CacheFilter cacheFilter;
    private ImageView imageView;
    private Bitmap bitmap;
    private CacheMat cacheMat;

    public ConfigFilterAdapter(Context context, CacheFilter cacheFilter, ImageView imageView, Bitmap bitmap) {
        this.context = context;
        this.cacheFilter = cacheFilter;
        this.imageView = imageView;
        this.bitmap = bitmap;
    }

    public ConfigFilterAdapter(Context context, CacheFilter cacheFilter, ImageView imageView, CacheMat cacheMat) {
        this.context = context;
        this.cacheFilter = cacheFilter;
        this.imageView = imageView;
        this.cacheMat = cacheMat;
    }

    @NonNull
    @Override
    public ConfigFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_config, parent, false);
        return new ConfigFilterAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ConfigFilterAdapter.ViewHolder holder, int position) {
        ConfigFilter configFilter = cacheFilter.getConfigFilter();
        if (configFilter.seekBars != null && configFilter.seekBars.size() > position) {
            ConfigFilter.SeekBar sb = configFilter.seekBars.get(position);
            holder.rcListSelection.setVisibility(View.GONE);
            holder.txNameSeekBar.setText(sb.name);

            holder.sbConfig.setMin(sb.minSeekBar);
            holder.sbConfig.setMax(sb.maxSeekBar);
            holder.sbConfig.setProgress(sb.value);

            holder.sbConfig.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    sb.setValue(seekBar.getProgress());
                    Convert.applyEffect(cacheFilter, bitmap, cacheMat, imageView);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            sb.setValue(seekBar.getProgress());
//                            Mat mat = new Mat();
//                            Utils.bitmapToMat(bitmap, mat);
//                            cacheFilter.getChangeImage().Filter(mat, configFilter);
//                            Bitmap bitmap = Convert.createBitmapFromMat(mat);
//                            cacheFilter.setBitmap(bitmap);
//                            imageView.setImageBitmap(bitmap);
//                        }
//                    }).start();
                }
            });
        } else {
            holder.sbConfig.setVisibility(View.GONE);
            holder.txNameSeekBar.setVisibility(View.GONE);
            SelectionAdapter selectionAdapter;
            if(bitmap != null){
                selectionAdapter = new SelectionAdapter(context, cacheFilter, bitmap, imageView);
            } else {
                selectionAdapter = new SelectionAdapter(context, cacheFilter, cacheMat, imageView);
            }
            holder.rcListSelection.setAdapter(selectionAdapter);
            holder.rcListSelection.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }

    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(cacheFilter.getConfigFilter().seekBars != null){
            size += cacheFilter.getConfigFilter().seekBars.size();
        }
        if (cacheFilter.getConfigFilter().selections != null) {
            size++;
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txNameSeekBar;
        SeekBar sbConfig;
        RecyclerView rcListSelection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txNameSeekBar = itemView.findViewById(R.id.txNameSeekBar);
            sbConfig = itemView.findViewById(R.id.sbConfig);
            rcListSelection = itemView.findViewById(R.id.rcListSelection);

        }
    }
}
