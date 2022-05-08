package com.example.facedetectioon.model;

import com.example.facedetectioon.model.cache.CacheDataFace;
import com.example.facedetectioon.model.cache.CacheFilter;
import com.example.facedetectioon.model.cache.CacheMat;

public interface IOperation {
    public void operate(CacheMat cacheMat, CacheDataFace cacheDataFace, ConfigFilter configFilter);
}
