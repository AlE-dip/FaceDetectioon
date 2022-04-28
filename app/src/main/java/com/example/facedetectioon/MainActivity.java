package com.example.facedetectioon;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.autofill.FillEventHistory;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;

import com.example.facedetectioon.convertor.FaceDetect;
import com.example.facedetectioon.model.Album;
import com.example.facedetectioon.model.GridImageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    public static String FACE_DETECTION = "FaceDetection";
    private ImageView imPicture;
    private BottomNavigationView navigationBar;
    public static ArrayList<Album> albums;

    static{
        if(OpenCVLoader.initDebug()){
            Log.d("Check","OpenCv configured successfully");
        } else{
            Log.d("Check","OpenCv doesn’t configured successfully");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        createView();

        File path = Environment.getExternalStorageDirectory();

        albums = new ArrayList<>();
        if(AskPermission.filePermission(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getListAlbum(path, albums);
                }
            }).start();
        }

//        File imFace = new File(path, "face4.jpg");

//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inMutable = true;
//        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bmFace = BitmapFactory.decodeFile(imFace.toString(), bmOptions);
//        imPicture.setImageBitmap(bmFace);
//
//        FaceDetect faceDetect = new FaceDetect();
//        faceDetect.drawFace(bmFace, imPicture);
//
//        Mat imMat = new Mat();
//        Utils.bitmapToMat(bmFace, imMat);
//
//        Utils.matToBitmap(imMat, bmFace);

        setActionView();
    }

    private void createView() {
        imPicture = findViewById(R.id.im_picture);
        navigationBar = findViewById(R.id.navigation_bar);
    }

    private void setActionView() {
        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mnGallery:
                        Intent intent = new Intent(MainActivity.this, ChosePictureActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.mnCamera:
                        break;
                    case R.id.mnVideo:
                        break;
                }
                return true;
            }
        });
    }

    private void getListAlbum(File file, ArrayList<Album> albums) {
        Queue<File[]> queueFile = new LinkedList<>();
        File[] arrFile = {file};
        queueFile.add(arrFile);

        while (queueFile.size() > 0){
            arrFile = queueFile.poll();

            for(int i = 0; i < arrFile.length; i++){

                if(arrFile[i].isDirectory()){
                    File[] dirFiles = arrFile[i].listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            File file2 = new File(dir, filename);
                            return file2.isDirectory() && !file2.isHidden() && !filename.startsWith(".");
                        }
                    });
                    if(dirFiles != null && dirFiles.length > 0){
                        queueFile.add(dirFiles);
                    }
                    File[] imageFiles = arrFile[i].listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.contains(".png") || filename.contains(".jpg");
                        }
                    });
                    if(imageFiles != null && imageFiles.length > 0){
                        Album album = new Album(arrFile[i].getName(), imageFiles);
                        albums.add(album);
                    }
                }
            }
        }
    }
}