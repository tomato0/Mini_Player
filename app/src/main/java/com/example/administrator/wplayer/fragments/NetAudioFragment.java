package com.example.administrator.wplayer.fragments;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.base.BaseFragment;
import com.example.administrator.wplayer.interfaces.StorageStateChangeCallback;
import com.example.administrator.wplayer.single.SpaceService;
import com.example.administrator.wplayer.utils.StringFilterUtil;
import com.example.administrator.wplayer.view.CircleProgressView;

import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetAudioFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "NetAudioFragment";

    private static final int MESSAGE_STORE_CHANGE = 1;


    private LinearLayout myStoreView;
    private CircleProgressView mLocalStoreView;
    private CircleProgressView mExternalStoreView;
    private RelativeLayout mStoreTextLayout;
    private LinearLayout mLocalStoreLayout;
    private LinearLayout mExternalStoreLayout;
    private View mHeaderMiddleView;
    private TextView mFreeStoreHintView;
    private TextView mFreeStoreSizeView;
    private TextView mExternalStoreText;
    private TextView mLocalStoreText;
    private TextView mFreeStoreUnitView;
    private SpaceService mSpaceService;
    private ValueAnimator valueAnimator;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_STORE_CHANGE:
                    showHeaderView();
                    break;
            }
        }
    };
    private LinearLayout btnAbout;

    public NetAudioFragment() {
        // Required empty public constructor
    }

    @Override
    public String getFragmentTitle() {
        return "关于";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_net_audio, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initData() {
        mSpaceService = SpaceService.getInstance();
        mSpaceService.init(getContext().getApplicationContext());
        DeepStoreChangeCallBack callBack = new DeepStoreChangeCallBack(this);
        mSpaceService.setStorageStateChangeCallBack(String.valueOf(this.hashCode()),callBack);
    }

    private void initView(View view) {
        myStoreView = (LinearLayout) view.findViewById(R.id.deeply_store_view);
        mLocalStoreView = (CircleProgressView) view.findViewById(R.id.local_store_detail);
        mExternalStoreView = (CircleProgressView) view.findViewById(R.id.sdCard_store_detail);
        mStoreTextLayout = (RelativeLayout) view.findViewById(R.id.store_text_layout);
        mLocalStoreLayout = (LinearLayout) view.findViewById(R.id.local_store_layout);
        mExternalStoreLayout = (LinearLayout) view.findViewById(R.id.sdCard_store_layout);
        mHeaderMiddleView = (View) view.findViewById(R.id.deeply_header_middle_view);


        mFreeStoreHintView = (TextView) view.findViewById(R.id.free_store_string_text);
        mFreeStoreSizeView = (TextView) view.findViewById(R.id.free_store_size_text);
        mExternalStoreText = (TextView) view.findViewById(R.id.sdCard_store_text);
        mLocalStoreText = (TextView) view.findViewById(R.id.local_store_text);
        mFreeStoreUnitView = (TextView) view.findViewById(R.id.free_store_unit_text);
        btnAbout = (LinearLayout) view.findViewById(R.id.about_btn);
        btnAbout.setOnClickListener(this);
//        showHeaderView();
    }

    private void showHeaderView() {
        boolean haveExternalStore = mSpaceService.externalMemoryAvailable();
        Log.d(TAG, "haveExternalStore=" + haveExternalStore);
        long internalAvailable = mSpaceService.getAvailableInternalMemorySize();
        long internalTotal = mSpaceService.getTotalInternalMemorySize();
        int internalUsedPercent = (int) (((float) (internalTotal - internalAvailable)) / internalTotal * 100);
        Log.d(TAG, "Internal ----> avilable=" + internalAvailable + "total=" +
                internalTotal + "percent=" + internalUsedPercent);
        mLocalStoreLayout.setVisibility(View.VISIBLE);
        showStorageView(mLocalStoreView, internalUsedPercent);
        mLocalStoreText.setText(String.format(getResources().getString(R.string.deeply_header_phone_store_free_hint_text),
                Formatter.formatFileSize(getContext(), internalAvailable)));
        if (haveExternalStore) {
            //TODO:有外部存储
            mHeaderMiddleView.setVisibility(View.VISIBLE);
            mStoreTextLayout.setVisibility(View.GONE);
            mLocalStoreText.setVisibility(View.VISIBLE);
            mExternalStoreLayout.setVisibility(View.VISIBLE);
            long externalAvailable = mSpaceService.getAvailableExternalMemorySize();
            long externalTotal = mSpaceService.getTotalExternalMemorySize();
            int externalUsedPercent = (int) (((float) (externalTotal - externalAvailable)) / externalTotal * 100);
            Log.d(TAG, "External ----> avilable=" + externalAvailable + "total=" +
                    externalTotal + "percent=" + externalUsedPercent);
            mExternalStoreView.setVisibility(View.VISIBLE);
            showStorageView(mExternalStoreView, externalUsedPercent);
            mExternalStoreText.setText(String.format(getResources().getString(R.string.deeply_header_sd_store_free_hint_text),
                    Formatter.formatFileSize(getContext(), externalAvailable)));
        } else {
            //TODO:无外部存储
            mHeaderMiddleView.setVisibility(View.GONE);
            mStoreTextLayout.setVisibility(View.VISIBLE);
            mExternalStoreLayout.setVisibility(View.GONE);
            mLocalStoreText.setVisibility(View.GONE);
            mFreeStoreHintView.setText(getResources().getString(R.string.deeply_header_no_sd_store_free_hint_text));
            String spaceSizeString = Formatter.formatFileSize(getContext(),
                    internalAvailable);
            String numString = StringFilterUtil.getNumStr(spaceSizeString);
            Float numSize = Float.parseFloat(numString);
            String numStr = String.format("%.1f", numSize);
            String unitStr = StringFilterUtil.filterAlphabet(spaceSizeString);
            mFreeStoreSizeView.setText(numStr);
            mFreeStoreUnitView.setText(unitStr);
        }
    }

    private void showStorageView(final CircleProgressView view, int progress) {
        valueAnimator = ValueAnimator.ofInt(0, progress);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                view.setProgress(animatedValue, String.valueOf(animatedValue),
                        getContext().getResources().getString(R.string.deeply_header_store_percent_text),
                        getContext().getResources().getString(R.string.deeply_header_store_used_text));
            }
        });
        valueAnimator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_btn:
                break;
        }
    }

    //弱引用，防止内存泄漏
    static class DeepStoreChangeCallBack implements StorageStateChangeCallback {
        private WeakReference<NetAudioFragment> mFragment;

        public DeepStoreChangeCallBack(NetAudioFragment f) {
            mFragment = new WeakReference<NetAudioFragment>(f);
        }

        @Override
        public void onStorageStateChange() {
            NetAudioFragment f = mFragment.get();
            Log.d(TAG, "Activity storage change");
            f.mHandler.sendEmptyMessage(MESSAGE_STORE_CHANGE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            Log.d(TAG, "show");
            if (valueAnimator != null){
                valueAnimator.reverse();
            }
        } else {
            Log.d(TAG, "hidden");
            if (null != mSpaceService) {
                showHeaderView();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
