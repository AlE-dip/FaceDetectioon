package com.example.facedetectioon.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facedetectioon.R;
import com.example.facedetectioon.convertor.Convert;
import com.example.facedetectioon.convertor.UtilFunction;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.cache.CacheMat;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class ListFilterAdapter extends RecyclerView.Adapter<ListFilterAdapter.ViewHolder> {

    private ArrayList<CacheFilter> cacheFilters;
    private Context context;
    private ImageView imageView;
    private Bitmap bitmap;
    private ViewHolder cacheViewClick;
    private RecyclerView rcListConfig;
    private CacheMat cacheMat;
    public CacheFilter cacheFilter;
    private int type;
    private int index;
    private Mat avatar;

    public ListFilterAdapter(int type, ArrayList<CacheFilter> cacheFilters, CacheFilter cacheFilter, Context context, Bitmap bitmap, ImageView imageView, RecyclerView rcListConfig) {
        this.type = type;
        this.cacheFilters = cacheFilters;
        this.context = context;
        this.imageView = imageView;
        this.bitmap = bitmap;
        this.rcListConfig = rcListConfig;
        this.cacheFilter = cacheFilter;
        index = 0;
    }

    public ListFilterAdapter(int type, ArrayList<CacheFilter> cacheFilters, Context context, CacheMat cacheMat, Mat avatar, CacheFilter cacheFilter, ImageView imageView, RecyclerView rcListConfig) {
        this.type = type;
        this.cacheFilters = cacheFilters;
        this.context = context;
        this.imageView = imageView;
        this.cacheMat = cacheMat;
        this.rcListConfig = rcListConfig;
        this.cacheFilter = cacheFilter;
        this.avatar = avatar;
        index = 0;
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
        CacheFilter cacheFilter = cacheFilters.get(position + index);
        if (cacheFilter.getType() == type || cacheFilter.getType() == CacheFilter.TYPE_ALL) {
            bindView(holder, position + index);
        } else {
            index++;
            for (int i = position + index; i < cacheFilters.size(); i++) {
                CacheFilter cf = cacheFilters.get(position + index);
                if (cf.getType() == type || cf.getType() == CacheFilter.TYPE_ALL) {
                    break;
                } else {
                    index++;
                }
            }
            bindView(holder, position + index);
        }
    }

    private void bindView(ViewHolder holder, int position) {
        CacheFilter cacheFilter = cacheFilters.get(position);
        holder.id = cacheFilter.getId();
        holder.imImage.setImageResource(R.color.white);
        holder.txName.setText(cacheFilter.getName());
        holder.imConfig.setVisibility(View.GONE);

        if(cacheFilter.getChangeImage() != null){
            Convert.applyEffect(cacheFilter, bitmap, avatar, holder.imImage);
        } else {
            holder.imImage.setImageBitmap(Convert.createBitmapFromMat(avatar));
        }

        holder.imImage.setOnClickListener(createListenerImage(this, cacheFilter, holder));
    }

    private View.OnClickListener createListenerImage(ListFilterAdapter listFilterAdapter, CacheFilter cacheFilter, ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type == CacheFilter.TYPE_IMAGE){
                    Convert.applyEffect(cacheFilter, bitmap, null, imageView);
                }

                listFilterAdapter.cacheFilter.setCache(cacheFilter);

                if (cacheViewClick != null && cacheViewClick.imConfig.getVisibility() == View.VISIBLE) {
                    cacheViewClick.imConfig.setVisibility(View.GONE);
                    rcListConfig.setVisibility(View.GONE);
                }
                cacheViewClick = holder;
                if (listFilterAdapter.cacheFilter.getConfigFilter() != null) {
                    holder.imConfig.setVisibility(View.VISIBLE);
                    holder.imConfig.setOnClickListener(createListenerConfig(listFilterAdapter.cacheFilter));
                }
            }
        };
    }

    public View.OnClickListener createListenerConfig(CacheFilter cacheFilter) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcListConfig.getVisibility() == View.VISIBLE) {
                    rcListConfig.setVisibility(View.GONE);
                } else {
                    rcListConfig.setVisibility(View.VISIBLE);
                    ConfigFilterAdapter configFilterAdapter;
                    if (type == CacheFilter.TYPE_IMAGE) {
                        configFilterAdapter = new ConfigFilterAdapter(context, cacheFilter, imageView, bitmap);
                    } else {
                        configFilterAdapter = new ConfigFilterAdapter(context, cacheFilter, imageView, cacheMat);
                    }
                    rcListConfig.setAdapter(configFilterAdapter);
                    rcListConfig.setLayoutManager(new LinearLayoutManager(context));
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        int size = 0;
        for (CacheFilter cacheFilter : cacheFilters) {
            if (cacheFilter.getType() == type || cacheFilter.getType() == CacheFilter.TYPE_ALL) {
                size++;
            }
        }
        return size;
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
