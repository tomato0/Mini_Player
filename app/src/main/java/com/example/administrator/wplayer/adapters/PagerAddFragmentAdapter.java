package com.example.administrator.wplayer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.administrator.wplayer.base.BaseFragment;

import java.util.List;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2016/10/18 0018.
 * com.example.administrator.wplayer.adapters
 * 功能、作用：ViewPager添加Fragment的Adapter
 */
public class PagerAddFragmentAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;

    public PagerAddFragmentAdapter(FragmentManager fm,List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getFragmentTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (fragments != null){
            ret = fragments.size();
        }
        return ret;
    }
}
