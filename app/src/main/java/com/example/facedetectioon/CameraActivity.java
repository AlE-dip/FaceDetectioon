package com.example.facedetectioon;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.facedetectioon.convertor.Convert;
import com.example.facedetectioon.convertor.FaceDetect;
import com.example.facedetectioon.convertor.Filter;
import com.example.facedetectioon.convertor.Paint;
import com.example.facedetectioon.convertor.UtilFunction;
import com.example.facedetectioon.model.CacheFilter;
import com.example.facedetectioon.model.cache.CacheMat;
import com.example.facedetectioon.model.cache.FaceContourData;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageView imDrawFace;
    private CacheMat cacheMat;
    private CacheFilter chooseCacheFilter;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        createView();

        cacheMat = new CacheMat();
        chooseCacheFilter = new CacheFilter();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fmFilter, new ListFilterFragment(CameraActivity.this, cacheMat, chooseCacheFilter, imDrawFace));
        ft.commit();

        startCamera();
    }

    public void startCamera() {
        FaceDetect faceDetect = new FaceDetect();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, faceDetect);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider, FaceDetect faceDetect) {

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                // insert your code here.
                @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
                if (mediaImage != null) {
                    long time = System.currentTimeMillis();

                    InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            cacheMat.mat = Convert.imageProxyToMat(imageProxy);
                            imageProxy.close();
                            //UtilFunction.paintFace(cacheMat.mat, FaceDetect.cacheDataFace);
                            Convert.applyEffect(chooseCacheFilter, null, cacheMat, imDrawFace);
                            imDrawFace.post(new Runnable() {
                                @Override
                                public void run() {
                                    imDrawFace.setImageBitmap(Convert.createBitmapFromMat(cacheMat.mat));
                                }
                            });
                        }
                    }).start();

                    //faceDetect.drawFace(inputImage, cacheMat, chooseCacheFilter, imDrawFace, imageProxy, time);

                }
                // after done, release the ImageProxy object
                // imageProxy.close();
            }
        });

        cameraProvider.bindToLifecycle((LifecycleOwner) CameraActivity.this, cameraSelector, imageAnalysis);

        imDrawFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                        .build();
//                cameraProvider.bindToLifecycle((LifecycleOwner) CameraActivity.this, cameraSelector, imageAnalysis, preview);
            }
        });
    }

    private void createView() {
        imDrawFace = findViewById(R.id.imDrawFace);
    }
}