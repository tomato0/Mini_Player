package com.example.administrator.wplayer.fragments;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import com.example.administrator.wplayer.R;

import com.example.administrator.wplayer.adapters.MovieListAdapter;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.interfaces.NetVideoCallBack;
import com.example.administrator.wplayer.models.MediaItem;
import com.example.administrator.wplayer.utils.NetVideoUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_net_video)
public class NetVideoFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private static final String TAG = "NetVideoFragment";
    private static final int MSG_VIDEO = 11;

    @ViewInject(R.id.radio_movie_class)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.movie_list)
    private ListView mListView;
    private List<MediaItem> mList;
    private MovieListAdapter adapter;
    private List<MediaItem> classes;
    private NetVideoUtils netVideoUtils;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_VIDEO:
                    MediaItem mediaItem = (MediaItem) msg.obj;
                    mList.add(mediaItem);
                    classes.add(mediaItem);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public NetVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public String getFragmentTitle() {
        return "网络视频";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mList = new ArrayList<>();
        classes = new ArrayList<>();
        netVideoUtils = NetVideoUtils.getInstance();
        getNetVideoData();
        super.onCreate(savedInstanceState);
    }

    private void getNetVideoData() {
        new Thread(){
            @Override
            public void run() {
                netVideoUtils.getDataMovie(new INetVideoCallBack(NetVideoFragment.this));
            }
        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = x.view().inject(this,inflater,container);

        adapter = new MovieListAdapter(getContext(), classes);
        mListView.setAdapter(adapter);
        mRadioGroup.setOnCheckedChangeListener(this);
        mListView.setOnItemClickListener(this);
        return view;
    }

    class INetVideoCallBack implements NetVideoCallBack{
        private WeakReference<NetVideoFragment> mFragment;
        INetVideoCallBack(NetVideoFragment fragment){
            mFragment = new WeakReference<NetVideoFragment>(fragment);
        }
        @Override
        public void getNetVideo(MediaItem mediaItem) {
            NetVideoFragment fragment = mFragment.get();
            if (null == fragment || mediaItem == null){
                return;
            }
            Message message = fragment.mHandler.obtainMessage(MSG_VIDEO);
            message.obj = mediaItem;
            fragment.mHandler.sendMessage(message);
        }
    }


    @Override
    public void onDestroy() {
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_js:
                judgeMovieClass("惊悚");
                break;
            case R.id.radio_action:
                judgeMovieClass("动作");
                break;
            case R.id.radio_fun:
                judgeMovieClass("喜剧");
                break;
            case R.id.radio_kh:
                judgeMovieClass("科幻");
                break;
            case R.id.radio_story:
                judgeMovieClass("剧情");
                break;
            case R.id.radio_love:
                judgeMovieClass("爱情");
                break;
            case R.id.radio_xy:
                judgeMovieClass("悬疑");
                break;
            case R.id.radio_qh:
                judgeMovieClass("奇幻");
                break;
            case R.id.radio_mx:
                judgeMovieClass("冒险");
                break;
            case R.id.radio_family:
                judgeMovieClass("家庭");
                break;
            case R.id.radio_kt:
                judgeMovieClass("动画");
                break;
            case R.id.radio_kb:
                judgeMovieClass("恐怖");
                break;
            case R.id.radio_fz:
                judgeMovieClass("犯罪");
                break;
            case R.id.radio_zj:
                judgeMovieClass("传记");
                break;
            case R.id.radio_gj:
                judgeMovieClass("歌舞");
                break;
        }
    }

    private void judgeMovieClass(String str) {
        classes.clear();
        for (int i = 0; i < mList.size(); i++) {
            MediaItem mediaItem = mList.get(i);
            List<String> type = mediaItem.getType();
            Log.d("TYPE", "onCheckedChanged: "+type.toString());
            if (type.contains(str)){
                classes.add(mediaItem);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){
        }else {
            startGroupAnimator();
            Log.d(TAG,"show");
        }
        super.onHiddenChanged(hidden);
    }

    private void startGroupAnimator() {
        ObjectAnimator animGroup = ObjectAnimator.ofFloat(mRadioGroup, "translationY", -1000, 0);
        animGroup.setDuration(2000);
        animGroup.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                onStartAnimList();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animGroup.start();
    }

    private void onStartAnimList() {
        ObjectAnimator animList = ObjectAnimator.ofFloat(mListView, "translationY", 1000, 0);
        animList.setDuration(2000);
        animList.start();
    }
}
