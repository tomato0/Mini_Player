package com.example.administrator.wplayer.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：wangshaoqiang on 2016/7/18 09:16
 * 作用：媒体类
 */
public class MediaItem implements Parcelable {

    private String name;

    private long duration;

    private long size;

    private String data;

    private String artist;

    private String desc;

    private String imageUrl;

    private Bitmap bitmap;

    private String lrc;

    private List<String> type;

    public MediaItem() {
    }

    public MediaItem(String name, long duration, long size, String data, String artist, String desc, String imageUrl, Bitmap bitmap) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.bitmap = bitmap;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", desc='" + desc + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", bitmap=" + bitmap +
                ", lrc='" + lrc + '\'' +
                ", type=" + type +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.duration);
        dest.writeLong(this.size);
        dest.writeString(this.data);
        dest.writeString(this.artist);
        dest.writeString(this.desc);
        dest.writeString(this.imageUrl);
        dest.writeParcelable(this.bitmap, flags);
        dest.writeString(this.lrc);
        dest.writeStringList(this.type);
    }

    protected MediaItem(Parcel in) {
        this.name = in.readString();
        this.duration = in.readLong();
        this.size = in.readLong();
        this.data = in.readString();
        this.artist = in.readString();
        this.desc = in.readString();
        this.imageUrl = in.readString();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.lrc = in.readString();
        this.type = in.createStringArrayList();
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
}
