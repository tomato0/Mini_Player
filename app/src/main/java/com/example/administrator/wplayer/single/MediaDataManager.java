package com.example.administrator.wplayer.single;

import com.example.administrator.wplayer.interfaces.VideoPlayInfo;
import com.example.administrator.wplayer.models.MediaItem;

import java.util.List;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/9 0009.
 * com.example.administrator.wplayer.single
 * 功能、作用：
 */

public class MediaDataManager {
    private static final String TAG = "MediaDataManager";

    private static MediaDataManager mInstance;
    private List<MediaItem> mediaItems;
    private List<MediaItem> audioMediaItems;

    public VideoPlayInfo getInfoCallBack() {
        return infoCallBack;
    }

    public void setInfoCallBack(VideoPlayInfo infoCallBack) {
        this.infoCallBack = infoCallBack;
    }

    private VideoPlayInfo infoCallBack;

    public static MediaDataManager getInstance(){
        if (null == mInstance){
            synchronized (MediaDataManager.class){
                if (null == mInstance){
                    mInstance = new MediaDataManager();
                }
            }
        }
        return mInstance;
    }

    public List<MediaItem> getVideoMediaItems() {
        return mediaItems;
    }

    public void setVideoMediaItems(List<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    public List<MediaItem> getAudioMediaItems() {
        return audioMediaItems;
    }

    public void setAudioMediaItems(List<MediaItem> audioMediaItems) {
        this.audioMediaItems = audioMediaItems;
    }
}
