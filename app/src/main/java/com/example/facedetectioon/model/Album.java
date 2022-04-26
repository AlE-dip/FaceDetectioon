package com.example.facedetectioon.model;

import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Album {
    private int id;
    private String name;
    private ArrayList<String> pathImages = new ArrayList<>();

    public Album(String name, File[] imageFiles) {
        this.name = name;
        for(int i = 0; i < imageFiles.length; i++){
            pathImages.add(imageFiles[i].getAbsolutePath());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
