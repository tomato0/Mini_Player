package com.example.administrator.wplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.adapters.AudioPagerAdapter;
import com.example.administrator.wplayer.models.MediaItem;
import com.example.administrator.wplayer.single.MediaDataManager;
import com.example.administrator.wplayer.utils.NetAudioUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {
    private static final String TAG = "AudioListActivity";

    private AudioPagerAdapter videoPagerAdapter;
    private static final int MESSAGE_AUDIO_SUCCESS = 10;
    private static final int MESSAGE_AUDIO_ERROR = 11;

    /**
     * 装数据集合
     */
    private List<MediaItem> mediaItems;

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_AUDIO_SUCCESS:
                    if(mediaItems != null && mediaItems.size() >0){
                        //有数据
                        //设置适配器
                        mMediaDataManager.setAudioMediaItems(mediaItems);
                        videoPagerAdapter = new AudioPagerAdapter(AudioListActivity.this,mediaItems,false);
                        listview.setAdapter(videoPagerAdapter);
                        //把文本隐藏
                        tv_nomedia.setVisibility(View.GONE);
                    }else{
                        //没有数据
                        //文本显示
                        tv_nomedia.setVisibility(View.VISIBLE);
                        tv_nomedia.setText("没有发现音频....");
                    }
                    break;
                case MESSAGE_AUDIO_ERROR:
                    String error = (String) msg.obj;
                    Toast.makeText(AudioListActivity.this, error, Toast.LENGTH_SHORT).show();
                    break;
            }


            //ProgressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };
    private MediaDataManager mMediaDataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        mMediaDataManager = MediaDataManager.getInstance();
        initView();
        initData();
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        tv_nomedia = (TextView) findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        //设置ListView的Item的点击事件
        listview.setOnItemClickListener(new MyOnItemClickListener());
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //3.传递列表数据-对象-序列化
            Intent intent = new Intent(AudioListActivity.this,AudioPlayerActivity.class);
            intent.putExtra("position",position);
            startActivity(intent);

        }
    }

    public void initData() {
        Log.d(TAG,"本地视频的数据被初始化了。。。");
        //加载本地视频数据
        String type = getIntent().getStringExtra("type");
        if (type.isEmpty()){
            getDataFromLocal();
        }else {
            NetAudioUtils netAudioUtils = NetAudioUtils.getInstance();
            netAudioUtils.getNetAudios(type,new INetAudioCallBack(this));
        }
    }

    private static class INetAudioCallBack implements NetAudioUtils.NetAudioCallBack{
        private WeakReference<AudioListActivity> mActivity;

        INetAudioCallBack(AudioListActivity activity){
            mActivity = new WeakReference<AudioListActivity>(activity);
        }
        @Override
        public void getAudioByNet(List<MediaItem> musics) {
            AudioListActivity activity = mActivity.get();
            if (null == activity){
                return;
            }
            activity.mediaItems = musics;
            activity.handler.sendEmptyMessage(MESSAGE_AUDIO_SUCCESS);
        }

        @Override
        public void getAudioError(String error) {
            AudioListActivity activity = mActivity.get();
            if (null == activity){
                return;
            }
            Message message = activity.handler.obtainMessage(MESSAGE_AUDIO_ERROR);
            message.obj = error;
            activity.handler.sendMessage(message);
        }
    }

    /**
     * 从本地的sdcard得到数据
     * //anim1.遍历sdcard,后缀名
     * //anim2.从内容提供者里面获取视频
     * //3.如果是6.0的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {

        new Thread(){
            @Override
            public void run() {
                super.run();

//                isGrantExternalRW((Activity) context);
//                SystemClock.sleep(2000);
                mediaItems = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){

                        MediaItem mediaItem = new MediaItem();

                        mediaItems.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);



                    }
                    cursor.close();
                }
                //Handler发消息
                handler.sendEmptyMessage(MESSAGE_AUDIO_SUCCESS);


            }
        }.start();

    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
}
