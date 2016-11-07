package com.example.administrator.wplayer.models;

import android.graphics.Bitmap;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2016/10/20 0020.
 * com.example.administrator.wplayer.models
 * 功能、作用：本地视频信息
 */
public class LocalVideoData {
    private String name;
    private float size;
    private String duration;
    private String data;
    private Bitmap bitmap;

    public LocalVideoData(String name, float size, String duration, String data, Bitmap bitmap) {
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.data = data;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public float getSize() {
        return size;
    }

    public String getDuration() {
        return duration;
    }

    public String getData() {
        return data;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "LocalVideoData{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", duration='" + duration + '\'' +
                ", data='" + data + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }
}
