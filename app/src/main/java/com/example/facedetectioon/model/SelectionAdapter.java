package com.example.facedetectioon.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facedetectioon.R;
import com.example.facedetectioon.convertor.Convert;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder>{

    private Context context;
    private CacheFilter cacheFilter;
    private ImageView imageView;
    private Bitmap bitmap;
    private ViewHolder cacheViewCLick;

    public SelectionAdapter(Context context, CacheFilter cacheFilter, Bitmap bitmap, ImageView imageView) {
        this.context = context;
        this.cacheFilter = cacheFilter;
        this.bitmap = bitmap;
        this.imageView = imageView;
    }

    @NonNull
    @Override
    public SelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selection, parent, false);
        return new SelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionAdapter.ViewHolder holder, int position) {
        ConfigFilter.Selection selection = cacheFilter.getConfigFilter().selections.get(position);
        holder.txSelector.setText(selection.name);

        holder.txSelector.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(cacheViewCLick != null && cacheViewCLick.isClick){
                    cacheViewCLick.txSelector.setBackgroundColor(context.getColor(R.color.non));
                    cacheViewCLick = holder;
                } else {
                    cacheViewCLick = holder;
                }
                holder.isClick = true;
                holder.txSelector.setBackgroundColor(context.getColor(R.color.gray));

                cacheFilter.getConfigFilter().setSelected(selection.value);
                Bitmap bmNew = Convert.applyEffect(cacheFilter, bitmap, imageView);

                if(bmNew != null){
                    imageView.setImageBitmap(bmNew);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cacheFilter.getConfigFilter().selections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txSelector;
        public boolean isClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txSelector = itemView.findViewById(R.id.txSelector);
            isClick = false;
        }
    }
}
