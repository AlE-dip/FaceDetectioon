package com.example.facedetectioon.model;

import java.util.ArrayList;

public class ConfigFilter {
    public ArrayList<Selection> selections;
    public ArrayList<SeekBar> seekBars;

    public class Selection{
        int value;
        String name;
    }

    public class SeekBar{
        public boolean seekBar;
        public int minSeekBar;
        public int maxSeekBar;
    }
}
