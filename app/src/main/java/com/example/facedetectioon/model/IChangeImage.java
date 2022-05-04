package com.example.facedetectioon.model;

import org.opencv.core.Mat;

public interface IChangeImage {
    public boolean Filter(Mat mat, Mat dst, ConfigFilter configFilter);
}
