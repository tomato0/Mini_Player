package com.example.administrator.wplayer.fragments;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wplayer.MainActivity;
import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.adapters.LocalVideoListAdapter;
import com.example.administrator.wplayer.adapters.RecycleAdapter;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.customview.CustomSurfaceView;
import com.example.administrator.wplayer.models.LocalVideoData;
import com.example.administrator.wplayer.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseFragment implements Runnable, RecycleAdapter.OnChildVideoClickListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private static final String TAG = "VideoFragment";
    private List<LocalVideoData> mVideo;
    private static final int FIND_VIDEO = 1;
    private static final int VIDEO_PLAY_TIME = 2;
    private static final int ISLOOK_COLLECTION = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_VIDEO:
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
                case VIDEO_PLAY_TIME:
                    if (mMediaPlayer != null){
                        int currentPosition = mMediaPlayer.getCurrentPosition();
                        int duration = mMediaPlayer.getDuration();
                        mPlayTimeTxt.setText(mTimeUtils.stringForTime(currentPosition) + "/" + mTimeUtils.stringForTime(duration));
                        mVideoProgress.setProgress(currentPosition);
                        handler.sendEmptyMessage(VIDEO_PLAY_TIME);
                    }
                    break;
                case ISLOOK_COLLECTION:
                    isLookCollector = false;
                    mVideoCollector.setVisibility(View.GONE);
                    break;
            }
        }
    };
    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private ProgressBar progressBar;
    private CustomSurfaceView mVideoWindow;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mVideoHolder;
    private Utils mTimeUtils;
    int playPosition = -1;
    private TextView mPlayTimeTxt;
    private SeekBar mVideoProgress;
    private FloatingActionButton mVideoPlayBtn;
    private TextView mVideoTitle;
    private LocalVideoData playVideo;
    private LinearLayout mVideoCollector;
    private boolean isLookCollector = false;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideo = new ArrayList<>();
        mMediaPlayer = new MediaPlayer();
        mTimeUtils = new Utils();
        Thread thread = new Thread(this);
        thread.start();

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
        initView(view);
        mVideoHolder = mVideoWindow.getHolder();
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.local_video_list);
        progressBar = (ProgressBar) view.findViewById(R.id.local_video_progress);
        mVideoWindow = (CustomSurfaceView) view.findViewById(R.id.video_top_window);
        mPlayTimeTxt = (TextView) view.findViewById(R.id.txt_video_time);
        mVideoProgress = (SeekBar) view.findViewById(R.id.video_progress);
        mVideoPlayBtn = (FloatingActionButton) view.findViewById(R.id.floating_play_btn);
        mVideoPlayBtn.setEnabled(false);
        mVideoTitle = (TextView) view.findViewById(R.id.video_title);
        mVideoCollector = (LinearLayout) view.findViewById(R.id.video_collector);
        setListener();

    }

    private void setListener() {
        mVideoWindow.setOnClickListener(this);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mVideoPlayBtn.setOnClickListener(this);
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
        Message message = handler.obtainMessage(FIND_VIDEO);
        message.obj = dataList;
        handler.sendMessage(message);
    }


    //视频列表Item监听
    @Override
    public void onChildClickListener(RecyclerView recyclerView, View itemView, int position, LocalVideoData videoData) {
        if (playPosition != position){
            playVideo = videoData;
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            String videoUrl = videoData.getData();
            playPosition = position;
            if (mVideoHolder != null){
                mMediaPlayer.setDisplay(mVideoHolder);
            }
            try {
                mMediaPlayer.setDataSource(getContext(),Uri.parse(videoUrl));
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.start();
            int duration = mMediaPlayer.getDuration();
            mVideoProgress.setMax(duration);
            mVideoPlayBtn.setEnabled(true);
            mVideoPlayBtn.setImageResource(R.mipmap.pause_icon);
            mVideoTitle.setText(videoData.getName());
            handler.sendEmptyMessage(VIDEO_PLAY_TIME);
        }
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        mMediaPlayer = null;
        super.onDestroy();
    }

    //seekBar进度监听
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){//fromUser--->用户操作改变seekBar进度时返回true
            mMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_play_btn:
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                    mVideoPlayBtn.setImageResource(R.mipmap.play_icon);
                    mVideoTitle.setText("暂停中...");
                }else if (!mMediaPlayer.isPlaying()){
                    mMediaPlayer.start();
                    mVideoPlayBtn.setImageResource(R.mipmap.pause_icon);
                    mVideoTitle.setText(playVideo.getName());
                }
                break;
            case R.id.video_top_window:
                if (!isLookCollector){
                    mVideoCollector.setVisibility(View.VISIBLE);
                }
                isLookCollector = true;
                break;
        }
    }
}
