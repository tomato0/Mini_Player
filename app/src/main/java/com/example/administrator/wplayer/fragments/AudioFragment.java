package com.example.administrator.wplayer.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.wplayer.IMusicPlayerService;
import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.activity.AudioListActivity;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.models.MusicType;
import com.example.administrator.wplayer.service.MusicPlayerService;
import com.example.administrator.wplayer.single.MediaDataManager;
import com.example.administrator.wplayer.utils.AnimatorUtils;
import com.example.administrator.wplayer.utils.NetAudioUtils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "AudioFragment";

    private IMusicPlayerService service;//服务的代理类，通过它可以调用服务的方法
    private ServiceConnection con;
    private MediaDataManager mediaDataManager;
    private NetAudioUtils netAudioUtils;
    private List<MusicType> mMusicTypes;
    private ObjectAnimator a1;
    private ObjectAnimator a2;
    private ObjectAnimator a3;
    private ObjectAnimator a4;


    private FrameLayout localMusic;
    private FrameLayout netMusic1;
    private TextView netMusicTxt1;
    private FrameLayout netMusic2;
    private TextView localText;
    private TextView netMusicTxt2;
    private FrameLayout netMusic3;
    private TextView netMusicTxt3;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private AnimatorUtils animatorUtils;

    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public String getFragmentTitle() {
        return "音乐";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        animatorUtils = AnimatorUtils.getInstance();
        findViews(view);
        initListener();
        initAudioType();
        mediaDataManager = MediaDataManager.getInstance();
        if (null != mediaDataManager.getAudioMediaItems()){
            initConnection();
            bindAndStartService();
        }
        return view;
    }

    private void initData() {
        localText.setText(mMusicTypes.get(0).getName());
        netMusicTxt1.setText(mMusicTypes.get(1).getName());
        netMusicTxt2.setText(mMusicTypes.get(2).getName());
        netMusicTxt3.setText(mMusicTypes.get(3).getName());
        localMusic.setOnClickListener(this);
        netMusic1.setOnClickListener(this);
        netMusic2.setOnClickListener(this);
        netMusic3.setOnClickListener(this);
    }

    private void findViews(View view) {
        localMusic = (FrameLayout) view.findViewById( R.id.local_music );
        localText = (TextView) view.findViewById( R.id.local_text );
        netMusic1 = (FrameLayout) view.findViewById( R.id.net_music1 );
        netMusicTxt1 = (TextView) view.findViewById( R.id.net_music_txt1 );
        netMusic2 = (FrameLayout) view.findViewById( R.id.net_music2 );
        netMusicTxt2 = (TextView) view.findViewById( R.id.net_music_txt2 );
        netMusic3 = (FrameLayout) view.findViewById( R.id.net_music3 );
        netMusicTxt3 = (TextView) view.findViewById( R.id.net_music_txt3 );
    }

    private void localAnimator() {
        a1 = animatorUtils.rotateViewAnimator(localMusic, -90, 0, 300, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                netMusic1Animator();
            }
        });
    }

    private void netMusic1Animator() {
        a2 = animatorUtils.rotateViewAnimator(netMusic1, -90, 0, 300, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                netMusic2Animator();
            }
        });
    }

    private void netMusic2Animator() {
        a3 = animatorUtils.rotateViewAnimator(netMusic2, -90, 0, 300, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                netMusic3Animator();
            }
        });
    }

    private void netMusic3Animator() {
        a4 = animatorUtils.rotateViewAnimator(netMusic3, -90, 0, 300, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
    }

    private void initListener() {

    }

    private void initConnection() {
        if (con != null){
            return;
        }
        con = new ServiceConnection() {

            /**
             * 当连接成功的时候回调这个方法
             * @param name
             * @param iBinder
             */
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                service = IMusicPlayerService.Stub.asInterface(iBinder);
            }

            /**
             * 当断开连接的时候回调这个方法
             * @param name
             */
            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    if(service != null){
                        service.stop();
                        service = null;
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initAudioType() {
        netAudioUtils = NetAudioUtils.getInstance();
        mMusicTypes = netAudioUtils.getNetAudioByType();
        initData();

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), AudioListActivity.class);
        switch (v.getId()) {
            case R.id.local_music:
                intent.putExtra("type","");
                break;
            case R.id.net_music1:
                intent.putExtra("type",mMusicTypes.get(1).getId());
                break;
            case R.id.net_music2:
                intent.putExtra("type",mMusicTypes.get(2).getId());
                break;
            case R.id.net_music3:
                intent.putExtra("type",mMusicTypes.get(3).getId());
                break;
        }
        startActivity(intent);
    }

    private void bindAndStartService() {
        Intent intent = new Intent(getContext(), MusicPlayerService.class);
        intent.setAction("com.wsq.mobileplayer_OPENAUDIO");
        getContext().bindService(intent, con, Context.BIND_AUTO_CREATE);
        getContext().startService(intent);//不至于实例化多个服务
    }

    @Override
    public void onDestroy() {
        if(con != null){
            getContext().unbindService(con);
            con = null;
        }

        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (mMusicTypes != null){
            if (!hidden && animatorUtils != null){
                localAnimator();
            }
        }
        if (hidden){
            showCollectorView();
            if (a1 != null) {
                a1.cancel();
            }
            if (a2 != null) {
                a2.cancel();
            }
            if (a3 != null) {
                a3.cancel();
            }
            if (a4 != null) {
                a4.cancel();
            }
        }
        super.onHiddenChanged(hidden);
    }

    private void showCollectorView() {

    }
}
