package com.example.facedetectioon.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facedetectioon.R;

public class ConfigFilterAdapter extends RecyclerView.Adapter<ConfigFilterAdapter.ViewHolder> {
    @NonNull
    @Override
    public ConfigFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ConfigFilterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigFilterAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
