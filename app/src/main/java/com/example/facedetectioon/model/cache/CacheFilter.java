package com.example.facedetectioon.model.cache;

import android.graphics.Bitmap;

import com.example.facedetectioon.model.ConfigFilter;
import com.example.facedetectioon.model.ContentShare;
import com.example.facedetectioon.model.IChangeImage;
import com.example.facedetectioon.model.IDetectFace;
import com.example.facedetectioon.model.IOperation;

public class CacheFilter {
    public static int TYPE_IMAGE = 1;
    public static int TYPE_CAMERA = 2;
    public static int TYPE_ALL = 3;
    private long id;
    private String name;
    private Bitmap bitmap;
    private ConfigFilter configFilter;
    private IChangeImage changeImage;
    private IDetectFace detectFace;
    private IOperation operation;
    private int type;


    public CacheFilter(int type, String name, ConfigFilter configFilter, IChangeImage changeImage, IDetectFace detectFace, IOperation operation) {
        this.type = type;
        this.id = ContentShare.getMaxId();
        this.name = name;
        this.configFilter = configFilter;
        this.changeImage = changeImage;
        this.detectFace = detectFace;
        this.operation = operation;
    }

    public CacheFilter() {
        this.type = 0;
        this.id = 0;
        this.name = "";
        this.configFilter = null;
        this.changeImage = null;
        this.detectFace = null;
        this.operation = null;
    }

    public void setCache(CacheFilter cacheFilter){
        type = cacheFilter.getType();
        id = cacheFilter.getId();
        name = cacheFilter.getName();
        configFilter = cacheFilter.getConfigFilter();
        changeImage = cacheFilter.getChangeImage();
        detectFace = cacheFilter.getDetectFace();
        operation = cacheFilter.getOperation();
    }

    public IOperation getOperation() {
        return operation;
    }

    public void setOperation(IOperation operation) {
        this.operation = operation;
    }

    public IDetectFace getDetectFace() {
        return detectFace;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDetectFace(IDetectFace detectFace) {
        this.detectFace = detectFace;
    }

    public ConfigFilter getConfigFilter() {
        return configFilter;
    }

    public void setConfigFilter(ConfigFilter configFilter) {
        this.configFilter = configFilter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public IChangeImage getChangeImage() {
        return changeImage;
    }

    public void setChangeImage(IChangeImage changeImage) {
        this.changeImage = changeImage;
    }
}
