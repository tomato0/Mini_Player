package com.example.administrator.wplayer.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wplayer.MainActivity;
import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.activity.SystemVideoPlayer;
import com.example.administrator.wplayer.activity.VitamioVideoPlayer;
import com.example.administrator.wplayer.adapters.LocalVideoListAdapter;
import com.example.administrator.wplayer.adapters.RecycleAdapter;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.customview.CustomSurfaceView;
import com.example.administrator.wplayer.interfaces.VideoPlayInfo;
import com.example.administrator.wplayer.models.MediaItem;
import com.example.administrator.wplayer.single.MediaDataManager;
import com.example.administrator.wplayer.utils.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseFragment implements Runnable, RecycleAdapter.OnChildVideoClickListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, SurfaceHolder.Callback {
    private static final String TAG = "VideoFragment";
    private ArrayList<MediaItem> mVideo;
    private static final int FIND_VIDEO = 1;
    private static final int VIDEO_PLAY_TIME = 2;
    private static final int ISLOOK_COLLECTION = 3;
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
    private MediaItem playVideo;
    private LinearLayout mVideoCollector;
    private ImageView mFullScreenBtn;
    private MediaDataManager mMediaDataManager;
    private static final int REQUEST_CODE = 1;
    private static final int SYSTEMVIDEOPLAYER_RESULTCODE = 11;
    private int mPlay_current_position;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_VIDEO:
                    mVideo = (ArrayList<MediaItem>) msg.obj;
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
                    mVideoCollector.setVisibility(View.GONE);
                    break;
            }
        }
    };
    private VideoPlayCallBack playCallBack;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideo = new ArrayList<>();
        mMediaDataManager = MediaDataManager.getInstance();
        mMediaPlayer = new MediaPlayer();
        mTimeUtils = new Utils();
        playCallBack = new VideoPlayCallBack(this);
        mMediaDataManager.setInfoCallBack(playCallBack);
        Thread thread = new Thread(this);
        thread.start();
    }

    class VideoPlayCallBack implements VideoPlayInfo{
        private WeakReference<VideoFragment> mFragment;

        public VideoPlayCallBack(VideoFragment fragment){
            mFragment = new WeakReference<VideoFragment>(fragment);
        }
        @Override
        public void videoMessage(int position, int currentPosition) {
            VideoFragment fragment = mFragment.get();
            if (null == fragment){
                return;
            }
            reInitData();
            playPosition = position;
            playVideo = mVideo.get(position);
            mPlay_current_position = currentPosition;

        }
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
        mFullScreenBtn = (ImageView) view.findViewById(R.id.full_screen_btn);
        setListener();

    }

    private void setListener() {
        mVideoWindow.setOnClickListener(this);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mVideoPlayBtn.setOnClickListener(this);
        mFullScreenBtn.setOnClickListener(this);
        mVideoWindow.setListener(new CustomSurfaceView.TouchListener() {
            @Override
            public void onSurfaceViewTouchListener_UP() {
                mVideoCollector.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(ISLOOK_COLLECTION,4000);
            }

            @Override
            public void onSurfaceViewTouchListener_MOVE() {

            }

            @Override
            public void onSurfaceViewTouchListener_DOWN() {
                mVideoCollector.setVisibility(View.VISIBLE);
            }

        });

    }

    public List<MediaItem> getMediaItem(){
        //从数据库读取本地视频相关信息
        if (!isGrantExternalRW(getActivity())){
            getActivity().finish();
            return null;
        }
        List<MediaItem> list = new ArrayList<>();
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
        if (cursor != null) {
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
                MediaItem videoData = new MediaItem(name,duration, ((long) size),data,null,null,null,bitmap);
                list.add(videoData);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    @Override
    public void run() {
        //在子线程完成查找操作
        List<MediaItem> dataList = getMediaItem();
        Message message = handler.obtainMessage(FIND_VIDEO);
        message.obj = dataList;
        handler.sendMessage(message);
    }


    //视频列表Item监听
    @Override
    public void onChildClickListener(RecyclerView recyclerView, View itemView, int position, MediaItem videoData) {
        if (playPosition != position){
            playVideo = videoData;
            toPlay(position,videoData);
        }
    }

    private void toPlay(int position, MediaItem videoData) {
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
            mMediaPlayer.setDataSource(getContext(), Uri.parse(videoUrl));
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
                break;
            case R.id.full_screen_btn:
                if (mVideo == null && playPosition < 0){
                    return;
                }

//                Intent intent = new Intent(getActivity(),SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(mVideo.get(playPosition).getData()),"video/*");
//                intent.putParcelableArrayListExtra("videolist",mVideo);
//                intent.putExtra("position",playPosition);
//                getContext().startActivity(intent);
                Log.d(TAG,"click full screen");
//                Intent intent = new Intent(getActivity(),SystemVideoPlayer.class);
//                getContext().startActivity(intent);

                Intent intent = new Intent(getActivity(),VitamioVideoPlayer.class);
                if (mMediaDataManager != null){
                    mMediaDataManager.setMediaItems(mVideo);
                }
                intent.putExtra("position",playPosition);
                intent.putExtra("current_position",mMediaPlayer.getCurrentPosition());
                release();
                startActivityForResult(intent,REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            switch (resultCode){
                case SYSTEMVIDEOPLAYER_RESULTCODE:
                    playPosition = data.getIntExtra("play_position", 0);
                    if (mVideo != null){
                        playVideo = mVideo.get(playPosition);
                        mPlay_current_position = data.getIntExtra("play_current_position", 0);
                        reInitData();
                    }
                    break;
            }
        }
    }

    private void reInitData() {
        if (null == mMediaPlayer){
            mMediaPlayer = new MediaPlayer();
        }
        if (mVideoHolder == null){
            mVideoHolder = mVideoWindow.getHolder();
            mVideoHolder.addCallback(this);
        }
        if (handler != null){
            handler.sendEmptyMessageDelayed(ISLOOK_COLLECTION,4000);
        }
    }

    private void release() {
        if (null != mMediaPlayer){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            if (mVideoHolder != null) {
                mVideoHolder = null;
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        toPlay(playPosition,playVideo);
        mMediaPlayer.seekTo(mPlay_current_position);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
