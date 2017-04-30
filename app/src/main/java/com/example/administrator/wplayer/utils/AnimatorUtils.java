package com.example.administrator.wplayer.utils;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/16 0016.
 * com.example.administrator.wplayer.utils
 * 功能、作用：
 */

public class AnimatorUtils {

    private static AnimatorUtils mInstance;

    public static AnimatorUtils getInstance(){
        if (mInstance == null){
            synchronized (AnimatorUtils.class){
                if (mInstance == null){
                    mInstance = new AnimatorUtils();
                }
            }
        }
        return mInstance;
    }


    public ObjectAnimator rotateViewAnimator(View view, float from, float to, int duration,
                                          AnimatorListenerAdapter listener){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "RotationY", from, to);
        anim.setDuration(duration);
        anim.addListener(listener);
        anim.start();
        return anim;
    }
}
