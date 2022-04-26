package com.example.facedetectioon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.example.facedetectioon.model.Album;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ChosePictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_picture);

        ArrayList<Album> albums = new ArrayList<Album>();
        File path = Environment.getExternalStorageDirectory();

        if(AskPermission.filePermission(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getListAlbum(path, albums);
//                    lsAlbum.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            albumAdapter.notifyDataSetChanged();
//                        }
//                    });
                }
            }).start();
        }
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