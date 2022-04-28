package com.example.facedetectioon.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facedetectioon.EditPictureActivity;
import com.example.facedetectioon.R;
import com.example.facedetectioon.convertor.Convert;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class ListFilterAdapter extends RecyclerView.Adapter<ListFilterAdapter.ViewHolder>{

    private ArrayList<CacheFilter> cacheFilters;
    private Context context;
    private ImageView imageView;
    private Bitmap bitmap;
    private ViewHolder cacheViewClick;
    private RecyclerView rcListConfig;

    public ListFilterAdapter(ArrayList<CacheFilter> cacheFilters, Context context, Bitmap bitmap, ImageView imageView, RecyclerView rcListConfig) {
        this.cacheFilters = cacheFilters;
        this.context = context;
        this.imageView = imageView;
        this.bitmap = bitmap;
        this.rcListConfig = rcListConfig;
    }

    @NonNull
    @Override
    public ListFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new ListFilterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFilterAdapter.ViewHolder holder, int position) {
        CacheFilter cacheFilter = cacheFilters.get(position);
        holder.id = cacheFilter.getId();
        holder.imImage.setImageResource(R.color.white);
        holder.txName.setText(cacheFilter.getName());
        holder.imConfig.setVisibility(View.GONE);

        if (cacheFilter.getBitmap() == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mat mat = new Mat();
                    Utils.bitmapToMat(bitmap, mat);
                    cacheFilter.getChangeImage().Filter(mat);
                    Bitmap bmNew = Convert.createBitmapFromMat(mat);

                    holder.imImage.post(new Runnable() {
                        @Override
                        public void run() {
                            if (holder.id == cacheFilter.getId()) {
                                holder.imImage.setImageBitmap(bmNew);
                            }
                            cacheFilter.setBitmap(bmNew);
                            holder.imImage.setOnClickListener(createListenerImage(cacheFilter, holder));
                        }
                    });
                }
            }).start();
        } else {
            holder.imImage.setImageBitmap(cacheFilter.getBitmap());
            holder.imImage.setOnClickListener(createListenerImage(cacheFilter, holder));
        }
    }

    private View.OnClickListener createListenerImage(CacheFilter cacheFilter, ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(cacheFilter.getBitmap());
                if(cacheViewClick != null && cacheViewClick.imConfig.getVisibility() == View.VISIBLE){
                    cacheViewClick.imConfig.setVisibility(View.GONE);
                    rcListConfig.setVisibility(View.GONE);
                }
                cacheViewClick = holder;
                if(cacheFilter.getConfigFilter() != null){
                    holder.imConfig.setVisibility(View.VISIBLE);
                    holder.imConfig.setOnClickListener(createListenerConfig());
                }
            }
        };
    }

    public View.OnClickListener createListenerConfig(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rcListConfig.getVisibility() == View.VISIBLE){
                    rcListConfig.setVisibility(View.GONE);
                } else {
                    rcListConfig.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return cacheFilters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        long id;
        ImageView imImage, imConfig;
        TextView txName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = -111;
            imImage = itemView.findViewById(R.id.imImage);
            txName = itemView.findViewById(R.id.txName);
            imConfig = itemView.findViewById(R.id.imConfig);
        }
    }
}
