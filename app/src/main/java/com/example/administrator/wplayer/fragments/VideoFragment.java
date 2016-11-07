package com.example.administrator.wplayer.fragments;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wplayer.MainActivity;
import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.adapters.LocalVideoListAdapter;
import com.example.administrator.wplayer.adapters.RecycleAdapter;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.models.LocalVideoData;
import com.example.administrator.wplayer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseFragment implements Runnable, RecycleAdapter.OnChildVideoClickListener {
    private static final String TAG = "VideoFragment";
    private List<LocalVideoData> mVideo;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 110:
                    mVideo = (List<LocalVideoData>) msg.obj;
                    if (mVideo != null){
                        //获取视频列表信息
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "handleMessage: "+mVideo.toString());
                        adapter = new RecycleAdapter(getContext(), mVideo);
                        adapter.setChildClickListener(VideoFragment.this);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);
                    }
                    break;
            }
        }
    };
    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private ProgressBar progressBar;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideo = new ArrayList<>();

    }

    @Override
    public String getFragmentTitle() {
        return "本地视频";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.local_video_list);
        progressBar = (ProgressBar) view.findViewById(R.id.local_video_progress);
        Thread thread = new Thread(this);
        thread.start();

        return view;
    }

    public List<LocalVideoData> getLocalVideoData(){
        //从数据库读取本地视频相关信息
        List<LocalVideoData> list = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA
        };
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
            float size = cursor.getFloat(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            size = size/1024/1024;
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            Utils timeUtils = new Utils();
            String time = timeUtils.stringForTime((int) duration);
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(resolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
            LocalVideoData videoData = new LocalVideoData(name,size,time,data,bitmap);
            list.add(videoData);
        }
        cursor.close();

        return list;
    }

    @Override
    public void run() {
        //在子线程完成查找操作
        List<LocalVideoData> dataList = getLocalVideoData();
        Message message = handler.obtainMessage(110);
        message.obj = dataList;
        handler.sendMessage(message);
    }


    //视频列表Item监听
    @Override
    public void onChildClickListener(RecyclerView recyclerView, View itemView, int position, LocalVideoData videoData) {
        Toast.makeText(getContext(), videoData.getName(), Toast.LENGTH_SHORT).show();
    }
}
