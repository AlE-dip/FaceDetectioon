package com.example.facedetectioon;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

import com.example.facedetectioon.model.Album;
import com.example.facedetectioon.model.AlbumAdapter;
import com.example.facedetectioon.model.GridImageAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ChosePictureActivity extends AppCompatActivity {

    private TextView txTitleAlbum;
    public ConstraintLayout ctListAlbum;
    public RecyclerView rcListAlbum, rcListImage;
    private AlbumAdapter albumAdapter;
    private GridImageAdapter gridImageAdapter;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_picture);

        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        createView();

        if (MainActivity.albums.size() > 0) {
            setAlbum();
        } else {
            CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long l) {
                    setAlbum();
                    cancel();
                }

                @Override
                public void onFinish() {
                    start();
                }
            }.start();
        }

        setActionView();
    }

    private void setAlbum() {
        albumAdapter = new AlbumAdapter(MainActivity.albums, this);
        rcListAlbum.setAdapter(albumAdapter);
        rcListAlbum.setLayoutManager(new LinearLayoutManager(this));

        gridImageAdapter = new GridImageAdapter(MainActivity.albums.get(0).getCacheImages(), this);
        rcListImage.setAdapter(gridImageAdapter);
        rcListImage.setLayoutManager(new GridLayoutManager(this, 3));

    }


    private void createView() {
        txTitleAlbum = findViewById(R.id.txTitleAlbum);
        ctListAlbum = findViewById(R.id.ctListAlbum);
        rcListAlbum = findViewById(R.id.rcListAlbum);
        rcListImage = findViewById(R.id.rcListImage);
    }

    private void setActionView() {
        txTitleAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ctListAlbum.getVisibility() == View.VISIBLE) {
                    ctListAlbum.setVisibility(View.GONE);
                } else {
                    ctListAlbum.setVisibility(View.VISIBLE);
                }
            }
        });
    }


}