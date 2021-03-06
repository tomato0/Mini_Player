package com.example.administrator.wplayer.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2016/12/10 0010.
 * com.example.administrator.wplayer.customview
 * 功能、作用：
 */

public class CustomSurfaceView extends SurfaceView {
    private TouchListener listener;
    public CustomSurfaceView(Context context) {
        super(context);
    }

    public void setListener(TouchListener listener) {
        this.listener = listener;
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                listener.onSurfaceViewTouchListener_DOWN();
                break;
            case MotionEvent.ACTION_MOVE:
                listener.onSurfaceViewTouchListener_MOVE();
                break;
            case MotionEvent.ACTION_UP:
                if (null != listener){
                    listener.onSurfaceViewTouchListener_UP();
                }
                break;
        }
        return true;
    }
    public interface TouchListener{
        void onSurfaceViewTouchListener_UP();
        void onSurfaceViewTouchListener_MOVE();
        void onSurfaceViewTouchListener_DOWN();
    }
}
