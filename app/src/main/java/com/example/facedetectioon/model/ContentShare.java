package com.example.facedetectioon.model;

public class ContentShare {
    private static long maxId = 0;

    public static synchronized long getMaxId() {
        maxId++;
        return maxId;
    }
}
