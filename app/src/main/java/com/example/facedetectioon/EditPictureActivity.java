package com.example.facedetectioon;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.app.Fragment;

import com.example.facedetectioon.convertor.Convert;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class EditPictureActivity extends AppCompatActivity {

    private ImageView imEditPicture;
    private FrameLayout fmConfig;
    private BottomNavigationView nvOption;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        createView();

        Intent intent = getIntent();
        String pathImage = intent.getStringExtra("path");

        Bitmap bitmap = Convert.readImage(pathImage);
        imEditPicture.setImageBitmap(bitmap);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmConfig, new ListFilterFragment(EditPictureActivity.this, bitmap, imEditPicture));
        ft.commit();
    }

    private void createView() {
        imEditPicture = findViewById(R.id.im_edit_picture);
        fmConfig = findViewById(R.id.fmConfig);
    }
}