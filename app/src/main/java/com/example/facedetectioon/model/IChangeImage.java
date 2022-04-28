package com.example.facedetectioon.model;

import org.opencv.core.Mat;

public interface IChangeImage {
    public void Filter(Mat mat, ConfigFilter configFilter);
}
