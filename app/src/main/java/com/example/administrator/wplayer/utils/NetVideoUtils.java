package com.example.administrator.wplayer.utils;

import android.util.Log;

import com.example.administrator.wplayer.interfaces.NetVideoCallBack;
import com.example.administrator.wplayer.models.MediaItem;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/29 0029.
 * com.example.administrator.wplayer.utils
 * 功能、作用：网络视频
 */

public class NetVideoUtils {

    private static NetVideoUtils instance;

    public static NetVideoUtils getInstance(){
        if (instance == null){
            synchronized (NetVideoUtils.class){
                if (instance == null){
                    instance = new NetVideoUtils();
                }
            }
        }
        return instance;
    }
    public void getDataMovie(final NetVideoCallBack callBack){
        String url = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CacheCallback<JSONObject>() {
            @Override
            public boolean onCache(JSONObject result) {
                return false;
            }

            @Override
            public void onSuccess(JSONObject result) {
                getNetVideos(result, callBack);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getNetVideos(JSONObject result, NetVideoCallBack callBack) {
        try {
            JSONArray trailers = result.getJSONArray("trailers");
            for (int i = 0; i < trailers.length(); i++) {
                JSONObject object = trailers.getJSONObject(i);
                MediaItem mediaItem = new MediaItem();
                String img = object.getString("coverImg");
                String url = object.getString("hightUrl");
                String name = object.getString("movieName");
                String summary = object.getString("summary");
                JSONArray types = object.getJSONArray("type");
                List<String> mediaType = new ArrayList<>();
                for (int j = 0; j < types.length(); j++) {
                    String type = types.getString(j);
                    mediaType.add(type);
                }
                mediaItem.setImageUrl(img);
                mediaItem.setData(url);
                mediaItem.setDesc(summary);
                mediaItem.setName(name);
                mediaItem.setType(mediaType);
                callBack.getNetVideo(mediaItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
