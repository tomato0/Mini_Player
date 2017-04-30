package com.example.administrator.wplayer.utils;//package com.example.administrator.wplayer.utils;

import android.util.Log;

import com.example.administrator.wplayer.interfaces.RetrofitService;
import com.example.administrator.wplayer.models.MediaItem;
import com.example.administrator.wplayer.models.MusicType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/12 0012.
 * com.example.administrator.wplayer.utils
 * 功能、作用：get net audio utils
 */

public class NetAudioUtils {
    private static final String TAG = "NetAudioUtils";
    private static final String NET_AUDIO_TYPE_BASE_URL = "https://route.showapi.com/";

    private static NetAudioUtils mInstance;

    public static NetAudioUtils getInstance(){
        if (null == mInstance){
            synchronized (NetAudioUtils.class){
                if (null == mInstance){
                    mInstance = new NetAudioUtils();
                }
            }
        }
        return mInstance;
    }

    public List<MusicType> getNetAudioByType(){
        List<MusicType> musicTypes = new ArrayList<>();
        MusicType local = new MusicType();
        local.setId("0");
        local.setName("本地音乐");
        musicTypes.add(local);

        MusicType nd = new MusicType();
        nd.setId("5");
        nd.setName("内地歌曲");
        musicTypes.add(nd);

        MusicType gt = new MusicType();
        gt.setId("6");
        gt.setName("港台歌曲");
        musicTypes.add(gt);

        MusicType hot = new MusicType();
        hot.setId("26");
        hot.setName("热门歌曲");
        musicTypes.add(hot);

        return musicTypes;
    }

    public void getNetAudios(final String type, final NetAudioCallBack callBack){
        new Thread(){
            @Override
            public void run() {
                Retrofit.Builder builder = new Retrofit.Builder();
                //设置基本配置
                Retrofit retrofit = builder.baseUrl(NET_AUDIO_TYPE_BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())  //基本数据类型转换器
                        .build();
                //接口实例
                //接口的方法---->call对象
                RetrofitService service = retrofit.create(RetrofitService.class);
                Call<String> audioTemp = service.getStringForAudios(type);
                Log.d(TAG,"type____>" + type);

                try {
                    Response<String> response = audioTemp.execute();
                    String audios = response.body();
                    Log.d(TAG,"audio:"+audios);
                    try {
                        List<MediaItem> musics = new ArrayList<MediaItem>();
                        JSONObject jsonObject = new JSONObject(audios);
                        JSONObject resBody = jsonObject.getJSONObject("showapi_res_body");
                        String res_error = jsonObject.getString("showapi_res_error");
                        JSONObject pagebean = resBody.getJSONObject("pagebean");
                        JSONArray musicList = pagebean.getJSONArray("songlist");
                        for (int i = 0; i < musicList.length(); i++) {
                            JSONObject object = musicList.getJSONObject(i);
                            String url = object.getString("url");
                            String songname = object.getString("songname");
                            String singername = object.getString("singername");
                            String songid = object.getString("songid");
                            String albumpic_small = object.getString("albumpic_small");
                            MediaItem mediaItem = new MediaItem();
                            mediaItem.setName(songname);
                            mediaItem.setData(url);
                            mediaItem.setArtist(singername);
                            mediaItem.setLrc(songid);
                            mediaItem.setImageUrl(albumpic_small);
                            musics.add(mediaItem);

                        }
                        if (callBack != null){
                            if (musics == null){
                                callBack.getAudioError(res_error);
                            }
                            callBack.getAudioByNet(musics);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public interface NetAudioCallBack{
        void getAudioByNet(List<MediaItem> musics);
        void getAudioError(String error);
    }

}
