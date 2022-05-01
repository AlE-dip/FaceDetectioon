package com.example.facedetectioon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AskPermission {

    public static final int REQUEST_FILE_CODE = 123;
    public static final int REQUEST_CAMERA_CODE = 124;

    public static boolean filePermission(Activity activity) {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FILE_CODE);
        }else {
            return true;
        }
        return false;
    }

    public static boolean cameraPermission(Activity activity) {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        }else {
            return true;
        }
        return false;
    }
}
