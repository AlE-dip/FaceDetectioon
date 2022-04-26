package com.example.facedetectioon;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;

import com.example.facedetectioon.convertor.FaceDetect;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static String FACE_DETECTION = "FaceDetection";
    private ImageView imPicture;

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

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imFace = new File(path, "face4.jpg");

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inMutable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmFace = BitmapFactory.decodeFile(imFace.toString(), bmOptions);
        imPicture.setImageBitmap(bmFace);

        FaceDetect faceDetect = new FaceDetect();
        faceDetect.drawFace(bmFace, imPicture);

        Mat imMat = new Mat();
        Utils.bitmapToMat(bmFace, imMat);

        Utils.matToBitmap(imMat, bmFace);
    }

    private void createView() {
        imPicture = findViewById(R.id.im_picture);
    }
}