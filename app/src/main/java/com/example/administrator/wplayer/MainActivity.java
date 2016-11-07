package com.example.administrator.wplayer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import com.example.administrator.wplayer.adapters.PagerAddFragmentAdapter;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.fragments.AudioFragment;
import com.example.administrator.wplayer.fragments.NetAudioFragment;
import com.example.administrator.wplayer.fragments.NetVideoFragment;
import com.example.administrator.wplayer.fragments.VideoFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private static final String TAG = "MainActivity";

    private RadioGroup mRadioGroup;
    private VideoFragment mVideoFragment;
    private AudioFragment mAudioFragment;
    private NetVideoFragment mNetVideoFragment;
    private NetAudioFragment mNetAudioFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.rb_video);


    }

    private void initView() {
        //初始化布局
        mRadioGroup = (RadioGroup) findViewById(R.id.bottom_tab_radio_group);

        mVideoFragment = new VideoFragment();
        mAudioFragment = new AudioFragment();
        mNetVideoFragment = new NetVideoFragment();
        mNetAudioFragment = new NetAudioFragment();

        //进入默认页显示
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        tx.add(R.id.main_pager,mVideoFragment);
        tx.commit();
    }

    /**
     * 底部Tab监听回调函数
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            case R.id.rb_video:
                transaction.replace(R.id.main_pager,mVideoFragment);
                break;
            case R.id.rb_audio:
                transaction.replace(R.id.main_pager,mAudioFragment);
                break;
            case R.id.rb_net_video:
                transaction.replace(R.id.main_pager,mNetVideoFragment);
                break;
            case R.id.rb_net_audio:
                transaction.replace(R.id.main_pager,mNetAudioFragment);
                break;
            default:
                transaction.replace(R.id.main_pager,mVideoFragment);
        }
        transaction.commit();
    }
}
