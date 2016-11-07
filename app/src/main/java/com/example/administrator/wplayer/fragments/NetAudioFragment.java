package com.example.administrator.wplayer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetAudioFragment extends BaseFragment {


    public NetAudioFragment() {
        // Required empty public constructor
    }

    @Override
    public String getFragmentTitle() {
        return "网络音乐";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_net_audio, container, false);
    }

}
